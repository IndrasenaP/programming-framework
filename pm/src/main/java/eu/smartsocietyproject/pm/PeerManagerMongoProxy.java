/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pm;

import com.mongodb.AggregationOptions;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
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
import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerQuery;
import eu.smartsocietyproject.peermanager.PeerQueryRule;
import eu.smartsocietyproject.peermanager.helper.SimplePeer;
import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;
import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.CollectiveBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.BsonArray;
import org.bson.BsonString;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.Document;

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
        collectivesCollection = db.getCollection("collective");
        peersCollection = db.getCollection("peer");
    }

    protected MongoDatabase getMongoDb() {
        return db;
    }

    public void close() {
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }

    private BsonArray convertMembers(Set<Peer> members) {
        BsonArray peers = new BsonArray();

        for (Peer p : members) {
            peers.add(new BsonString(p.getId()));
        }

        return peers;
    }

//    private Document convertAttributes(Map<String, Attribute> attributes) {
//        Document atts = new Document();
//
//        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
//            Document att = new Document();
//            att.append("value", entry.getValue().toString());
//            att.append("type", entry.getValue().getClass().getName());
//
//            atts.append(entry.getKey(), att);
//        }
//        return atts;
//    }
    private List<Document> convertAttributes(Map<String, Attribute> attributes) {
        List<Document> atts = new ArrayList<>();

        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
            Document att = new Document();
            att.append("key", entry.getKey());
            att.append("value", entry.getValue().toString());
            att.append("type", entry.getValue().getClass().getName());

            atts.add(att);
        }
        return atts;
    }
    
    private Map<String, Attribute> extractAttributesFromDocument(Document doc) {
        Object attributes = doc.get("attributes");
        if (!(attributes instanceof List)) {
            throw new UnsupportedOperationException();
        }
        
        Map<String, Attribute> atts = new HashMap<>();

        for (Document d : (List<Document>) attributes) {
            try {
                Class clazz = Class.forName(d.getString("type"));
                Attribute att = (Attribute) clazz.newInstance();
                att.parseValueFromString(d.getString("value"));
                atts.put(d.getString("key"), att);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(PeerManagerMongoProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return atts;
    }

    public void persistPeer(SimplePeer peer) {
        Document p = new Document("id", peer.getId());
        p.append("attributes", convertAttributes(peer.getAttributes()));
        peersCollection.insertOne(p);
    }

    @Override
    public void persistCollective(CollectiveBase collective) {
        Document doc = new Document("id", collective.getId());

        doc.put("peers", convertMembers(collective.getMembers()));
        doc.put("attributes", convertAttributes(collective.getAttributes()));

        collectivesCollection.insertOne(doc);
    }

    //todo-sv-QA: shouldn't this return a list?
    @Override
    public CollectiveIntermediary readCollectiveByQuery(PeerQuery query) {
        List<Document> atts = new ArrayList<>();
        for (PeerQueryRule rule : query.getPeerQueryRules()) {
            Document att = new Document("key", rule.getKey());
            att.append("value", rule.getAttribute().toString());
            att.append("type", rule.getAttribute().getClass().getName());
            atts.add(att);
        }

        FindIterable<Document> peers = peersCollection
                .find(Filters.all("attributes", atts));

//        FindIterable<Document> results = collectivesCollection
//                .find(Filters.all("attributes", atts));
        CollectiveIntermediary collective = new CollectiveIntermediary();

        for (Document p : peers) {
            SimplePeer peer = new SimplePeer(p.getString("id"));
            peer.addAll(extractAttributesFromDocument(p));
            collective.addMember(peer);
        }
        
        if(collective.getMembers().isEmpty()) {
            return null;
        }

        return collective;
    }

    @Override
    public CollectiveIntermediary readCollectiveById(String id) {
        Document doc = collectivesCollection.find(Filters.eq("id", id)).first();

        if (doc == null) {
            return null;
        }

        CollectiveIntermediary collective = new CollectiveIntermediary();
        collective.setId(doc.getString("id"));

        Object peersObject = doc.get("peers");
        if (!(peersObject instanceof List)) {
            return null;
        }

        for (String peerId : (List<String>) peersObject) {
            collective.addMember(new SimplePeer(peerId));
        }
        
        collective.getAttributes().putAll(extractAttributesFromDocument(doc));

        return collective;
    }

}
