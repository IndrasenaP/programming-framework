/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.helper.EntityCore;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;
import eu.smartsocietyproject.pf.helper.JSONCollectiveIntermediary;
import eu.smartsocietyproject.pf.helper.attributes.MongoMembersAttribute;
import eu.smartsocietyproject.peermanager.query.CollectiveQuery;
import eu.smartsocietyproject.peermanager.query.Query;
import eu.smartsocietyproject.pf.helper.InternalPeerManager;
import eu.smartsocietyproject.pf.helper.factory.AttributeFactory;
import eu.smartsocietyproject.pf.helper.factory.MongoAttributeFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class PeerManagerMongoProxy extends InternalPeerManager {
    private static ObjectMapper mapper = new ObjectMapper();
    private final AttributeFactory attributeFactory = new MongoAttributeFactory();
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


    @Override
    public void persistPeer(PeerIntermediary peer) {
        peersCollection.insertOne(Document.parse(jsonToString(peer.toJson())));
    }
    
    @Override
    public PeerIntermediary readPeerById(String peerId) throws PeerManagerException {
        Document doc = peersCollection.find(Filters
                .eq(MongoConstants.id, peerId)).first();
        
        if (doc == null) {
            throw new PeerManagerException(
                String.format("Collective not found: %s", peerId));
        }
        
        return PeerIntermediary.create(doc.toJson());
    }
    
    //todo-sv think about where this really belongs to
    //todo-sv think in generall if attribute should really work with JsonNode?
    //-> this restrains us from using other json frameworks
    protected static String jsonToString(JsonNode node) {
        try {
            return mapper.writeValueAsString(node);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void persistCollective(ApplicationBasedCollective collective) 
            throws PeerManagerException {
        //todo: maybe this can be merged with the other query code
        JSONCollectiveIntermediary ci = toCollectiveIntermediary(collective);
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.set(MongoConstants.id, JsonNodeFactory.instance.textNode(ci.getId()));
        
        collectivesCollection.replaceOne(Document.parse(jsonToString(node)), 
        Document.parse(jsonToString(ci.toJson())), 
        (new UpdateOptions()).upsert(true));
        //collectivesCollection.insertOne(Document.parse(jsonToString(ci.toJson())));
    }

    private JSONCollectiveIntermediary toCollectiveIntermediary(Collective collective) throws PeerManagerException {
        MongoMembersAttribute.Builder builder = MongoMembersAttribute.builder();
        for (Member member : collective.makeMembersVisible().getMembers()) {
            builder.addMember(member);
        }
        
        JSONCollectiveIntermediary.Builder collInterBuilder = 
                JSONCollectiveIntermediary.builder(builder.build());
        
        collective.getAttributes().entrySet().stream()
                .forEach(entry -> collInterBuilder.addAttribute(entry.getKey(), 
                        entry.getValue()));
        
        return collInterBuilder.build(collective.getId());
    }

    private Bson getAttributesMongoQuery(Query query) {
        List<Bson> filters = new ArrayList<>();
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        for (QueryRule rule : query.getQueryRules()) {
            node.set(rule.getKey(), rule.getAttribute().toJson());
        }
        String value = jsonToString(node);
        Document doc = Document.parse(value);
        filters.add(doc);
        return Filters.and(filters);
    }

    @Override
    public List<ResidentCollective> findCollectives(CollectiveQuery query) 
            throws PeerManagerException {
        FindIterable<Document> collectives = collectivesCollection
                .find(getAttributesMongoQuery(query));

        List<ResidentCollective> colls = new ArrayList<>();

        for (Document c : collectives) {
            colls.add(ResidentCollective
                    .createFromIntermediary(context, Optional.empty(), 
                            JSONCollectiveIntermediary.create(c.toJson(), 
                                            attributeFactory)));
        }

        return colls;
    }
    
    @Override
    public List<PeerIntermediary> findPeers(PeerQuery query) throws PeerManagerException {
        FindIterable<Document> peerDocs = this.peersCollection
                .find(getAttributesMongoQuery(query));
        
        List<PeerIntermediary> peers = new ArrayList<>();
        
        for(Document p: peerDocs) {
            System.out.println(p.toJson());
            peers.add(PeerIntermediary.create(p.toJson()));
        }
        
        return peers;
    }

    @Override
    public ApplicationBasedCollective createCollectiveFromQuery(PeerQuery query) throws PeerManagerException {
        return createCollectiveFromQuery(query, Optional.empty());
    }

    @Override
    public ApplicationBasedCollective createCollectiveFromQuery(PeerQuery query, String kind) throws PeerManagerException {
        return createCollectiveFromQuery(query, Optional.of(kind));
    }

    public ApplicationBasedCollective createCollectiveFromQuery(PeerQuery query, Optional<String> kind) throws PeerManagerException {
        FindIterable<Document> peers = peersCollection
            .find(getAttributesMongoQuery(query));

        MongoMembersAttribute.Builder builder = MongoMembersAttribute.builder();

        for (Document p : peers) {
            builder.addMember(PeerIntermediary
                                  .create(p.toJson()));
        }
        
        JSONCollectiveIntermediary.Builder collBuilder = 
                JSONCollectiveIntermediary.builder(builder.build());
        
        return ResidentCollective
                .createFromIntermediary(context, kind, collBuilder
                        .build(UUID.randomUUID().toString()))
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
        JSONCollectiveIntermediary collectiveIntermediary = 
                JSONCollectiveIntermediary.create(doc.toJson(), attributeFactory);
        
        //todo-sv: kind is hardcoded... alternatives?
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
