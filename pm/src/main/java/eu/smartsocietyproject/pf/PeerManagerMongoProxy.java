/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.helper.EntityCore;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.peermanager.helper.PeerIntermediary;
import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;
import eu.smartsocietyproject.peermanager.helper.MembersAttribute;
import eu.smartsocietyproject.peermanager.query.CollectiveQuery;
import eu.smartsocietyproject.peermanager.query.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;

/**
 * The PM-Mongo-Proxy provides a local MongoDB store where all persistence
 * operations are performed.
 *
 * The attribute mapping strategy is saving class information to this store.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerManagerMongoProxy implements PeerManager {
    private static ObjectMapper mapper = new ObjectMapper();
    private ApplicationContext context;
    private MongoDatabase db;
    private MongoCollection<Document> collectivesCollection;
    private MongoCollection<Document> peersCollection;

    private PeerManagerMongoProxy(ApplicationContext context, MongoDatabase db) {
        this.context = context;
        this.db = db;
        loadCollection();
    }

    private void loadCollection() {
        collectivesCollection = db.getCollection(MongoConstants.collection);
        peersCollection = db.getCollection(MongoConstants.peer);
    }

    protected MongoDatabase getMongoDb() {
        return db;
    }


    public void persistPeer(PeerIntermediary peer) {
        peersCollection.insertOne(Document.parse(jsonToString(peer.toJson())));
    }

    @Override
    public void persistCollective(ApplicationBasedCollective collective) {
        CollectiveIntermediary ci = toCollectiveIntermediary(collective);
        String json = jsonToString(ci.toJson());
        collectivesCollection.insertOne(Document.parse(json));
    }

    /* TODO raise exception */
    private String jsonToString(JsonNode node) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(EntityCore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    //todo-sv!attributes are not passed in!
    private CollectiveIntermediary toCollectiveIntermediary(Collective collective) {
        MembersAttribute.Builder builder = MembersAttribute.builder();
        for (Member member : collective.makeMembersVisible().getMembers()) {
            builder.addMember(member);
        }
        return CollectiveIntermediary.create(collective.getId(), builder.build());
    }

    private Bson getAttributesMongoQuery(Query query) {
        List<Bson> filters = new ArrayList<>();
        for (QueryRule rule : query.getQueryRules()) {
            filters.add(Filters.eq(rule.getKey(),
                                   rule.getAttribute().toJson()));
        }
        return Filters.and(filters);
    }

    @Override
    public List<ResidentCollective> findCollectives(CollectiveQuery query) {
        FindIterable<Document> collectives = collectivesCollection
                .find(getAttributesMongoQuery(query));

        List<CollectiveIntermediary> colls = new ArrayList<>();

        for (Document c : collectives) {
            colls.add(CollectiveIntermediary.create(c.toJson()));
        }

        return colls.stream()
                    .map(c -> ResidentCollective.createFromIntermediary(context, Optional.empty(), c))
                    .collect(Collectors.toList());
    }

    @Override
    public ApplicationBasedCollective createCollectiveFromQuery(PeerQuery query) {
        return createCollectiveFromQuery(query, Optional.empty());
    }

    @Override
    public ApplicationBasedCollective createCollectiveFromQuery(PeerQuery query, String kind) {
        return createCollectiveFromQuery(query, Optional.of(kind));
    }

    public ApplicationBasedCollective createCollectiveFromQuery(PeerQuery query, Optional<String> kind) {
        FindIterable<Document> peers = peersCollection
            .find(getAttributesMongoQuery(query));

        MembersAttribute.Builder builder = MembersAttribute.builder();

        for (Document p : peers) {
            builder.addMember(PeerIntermediary
                                  .createFromJson(p.toJson())
                                  .getId());
        }
        CollectiveIntermediary collectiveIntermediary = CollectiveIntermediary.create(builder.build());
        return
            ResidentCollective
                .createFromIntermediary(context, kind, collectiveIntermediary)
                .toApplicationBasedCollective();
    }


    @Override
    public ResidentCollective readCollectiveById(String id) throws PeerManagerException {
        Document doc = collectivesCollection.find(Filters
                .eq(MongoConstants.id, id)).first();

        if (doc == null) {
            throw new PeerManagerException(
                String.format("Collective not found: %s", id));
        }
        CollectiveIntermediary collectiveIntermediary = CollectiveIntermediary.create(doc.toJson());
        return ResidentCollective
            .createFromIntermediary(context, Optional.empty(), collectiveIntermediary);
    }

    public static Factory factory(MongoDatabase db) {
        return new Factory(db);
    }


    public static class Factory implements PeerManager.Factory {
        private final MongoDatabase db;

        private Factory(MongoDatabase db) {
            this.db = db;
        }

        @Override
        public PeerManagerMongoProxy create(ApplicationContext context) {
            return new PeerManagerMongoProxy(context, db);
        }
    }
}
