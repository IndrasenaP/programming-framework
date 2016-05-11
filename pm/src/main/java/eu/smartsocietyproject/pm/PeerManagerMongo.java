/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pm;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerQuery;
import eu.smartsocietyproject.peermanager.ResidentCollectiveIntermediary;
import eu.smartsocietyproject.pf.Collective;
import java.io.IOException;

/**
 * 
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerManagerMongo implements PeerManager {
	
	private MongoDatabase db;
	private MongodProcess mongoProcess;
	private MongodExecutable mongodExecutable;
	
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
		} catch (IOException ex) {
			close();
			throw ex;
		}
	}
	
	public void close() {
		if(mongodExecutable != null) {
			mongodExecutable.stop();
		}
	}

	@Override
	public void persistCollective(Collective collective) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
