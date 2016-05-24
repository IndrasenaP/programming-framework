/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pm;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
import eu.smartsocietyproject.peermanager.helper.ResidentCollectiveIntermediary;
import eu.smartsocietyproject.pf.CollectiveBase;
import java.io.IOException;
import org.bson.Document;
import org.json.JSONObject;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerManagerMongo implements PeerManager {

    private MongoDatabase db;
    private MongodProcess mongoProcess;
    private MongodExecutable mongodExecutable;
    private MongoCollection<Document> collectivesCollection;

    //todo: provide a constructor for passing in mongo
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

    public void close() {
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }

    @Override
    public void persistCollective(CollectiveBase collective) {
        Document doc = new Document("id", collective.getId());
        doc.put("peers", ConvertHelper
                .convertPeers(collective.getMembers()).toArray());
        doc.putAll(ConvertHelper.convertAttributes(collective.getAttributes()));
        collectivesCollection.insertOne(doc);
    }

    @Override
    public ResidentCollectiveIntermediary readCollectiveByQuery(PeerQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResidentCollectiveIntermediary readCollectiveById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
