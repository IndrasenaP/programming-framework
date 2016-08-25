/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pm;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.peermanager.helper.PeerIntermediary;
import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;
import eu.smartsocietyproject.peermanager.query.CollectiveQuery;
import eu.smartsocietyproject.peermanager.query.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    private MongoDatabase db;
    private MongodProcess mongoProcess;
    private MongodExecutable mongodExecutable;
    private MongoCollection<Document> collectivesCollection;
    private MongoCollection<Document> peersCollection;

    //todo: provide a constructor for passing in mongo
    //todo: is persisting over shutdowns needed?
    public PeerManagerMongoProxy(int mongoPort) throws IOException {
        try {
            MongodStarter starter = MongodStarter.getDefaultInstance();
            IMongodConfig mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(mongoPort, Network.localhostIsIPv6()))
                    .build();

            mongodExecutable = starter.prepare(mongodConfig);
            mongoProcess = mongodExecutable.start();

            MongoClient mongoClient = new MongoClient("localhost", mongoPort);
            db = mongoClient.getDatabase("smartSocietyLocalMongoDB");
            loadCollection();
        } catch (IOException ex) {
            close();
            throw ex;
        }
    }

    public PeerManagerMongoProxy(MongoDatabase db) {
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

    public void close() {
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }

    public void persistPeer(PeerIntermediary peer) {
        peersCollection.insertOne(Document.parse(peer.toJson()));
    }

    @Override
    public void persistCollective(CollectiveIntermediary collective) {
        collectivesCollection.insertOne(Document.parse(collective.toJson()));
    }

    private Bson getAttributesMongoQuery(Query query) {
        List<Bson> filters = new ArrayList<>();
        for (QueryRule rule : query.getQueryRules()) {
            filters.add(Filters.eq(rule.getKey(),
                    JSON.parse(rule.getAttribute().toJson())));
        }
        return Filters.and(filters);
    }

    @Override
    public List<CollectiveIntermediary> readCollectiveByQuery(CollectiveQuery query) {
        FindIterable<Document> collectives = collectivesCollection
                .find(getAttributesMongoQuery(query));

        List<CollectiveIntermediary> colls = new ArrayList<>();

        for (Document c : collectives) {
            colls.add(CollectiveIntermediary.create(c.toJson()));
        }

        return colls;
    }

    @Override
    public CollectiveIntermediary readCollectiveByQuery(PeerQuery query) {
        FindIterable<Document> peers = peersCollection
                .find(getAttributesMongoQuery(query));

        CollectiveIntermediary collective = CollectiveIntermediary.createEmpty();

        for (Document p : peers) {
            collective.addMember(PeerIntermediary.createFromJson(p.toJson()));
        }

        if (collective.getMembers().isEmpty()) {
            return null;
        }

        return collective;
    }

    @Override
    public CollectiveIntermediary readCollectiveById(String id) {
        Document doc = collectivesCollection.find(Filters
                .eq(MongoConstants.id, id)).first();

        if (doc == null) {
            return null;
        }

        return CollectiveIntermediary.create(doc.toJson());
    }
}
