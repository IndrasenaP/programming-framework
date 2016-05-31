/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pm;

import com.mongodb.MongoClient;
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
import eu.smartsocietyproject.peermanager.helper.SimplePeer;
import eu.smartsocietyproject.peermanager.helper.ResidentCollectiveIntermediary;
import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.CollectiveBase;
import java.io.IOException;
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
public class PeerManagerMongo implements PeerManager {

    private MongoDatabase db;
    private MongodProcess mongoProcess;
    private MongodExecutable mongodExecutable;
    private MongoCollection<Document> collectivesCollection;

    //todo: provide a constructor for passing in mongo
    //todo: is persisting over shutdowns needed?
    public PeerManagerMongo(int mongoPort) throws IOException {
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

    public PeerManagerMongo(MongoDatabase db) {
        this.db = db;
        loadCollection();
    }

    private void loadCollection() {
        collectivesCollection = db.getCollection("collective");
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
        
        for(Peer p: members) {
            peers.add(new BsonString(p.getId()));
        }
        
        return peers;
    }

    private Document convertAttributes(Map<String, Attribute> attributes) {
        Document atts = new Document();

        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
            Document att = new Document();
            att.append("value", entry.getValue().toString());
            att.append("type", entry.getValue().getClass().getName());

            atts.append(entry.getKey(), att);
        }
        return atts;
    }

    @Override
    public void persistCollective(CollectiveBase collective) {
        Document doc = new Document("id", collective.getId());

        //todo-sv: fix this! it is not a document that can be persisted
        doc.put("peers", convertMembers(collective.getMembers()));

        doc.put("attributes", convertAttributes(collective.getAttributes()));

        collectivesCollection.insertOne(doc);
    }

    @Override
    public ResidentCollectiveIntermediary readCollectiveByQuery(PeerQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResidentCollectiveIntermediary readCollectiveById(String id) {
        Document doc = collectivesCollection.find(Filters.eq("id", id)).first();

        if (doc == null) {
            return null;
        }

        ResidentCollectiveIntermediary collective = new ResidentCollectiveIntermediary();
        collective.setId(doc.getString("id"));

        Object peersObject = doc.get("peers");
        if (!(peersObject instanceof List)) {
            return null;
        }

        for (String peerId : (List<String>) peersObject) {
            collective.addMember(new SimplePeer(peerId));
        }

        Object attributes = doc.get("attributes");
        if (!(attributes instanceof Document)) {
            throw new UnsupportedOperationException();
        }

        for (Map.Entry<String, Object> entry : ((Document) attributes).entrySet()) {
            try {
                Document value = (Document) entry.getValue();
                Class clazz = Class.forName(value.getString("type"));
                Attribute att = (Attribute) clazz.newInstance();
                att.parseValueFromString(value.getString("value"));
                collective.addAttribute(entry.getKey(), att);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(PeerManagerMongo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return collective;
    }

}
