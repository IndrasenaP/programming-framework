package eu.smartsocietyproject.pf;

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

import java.io.IOException;

public class MongoRunner implements AutoCloseable {
    private final MongodExecutable mongodExecutable;
    private final MongoDatabase db;

    private MongoRunner(int port) throws IOException {
        try {
            MongodStarter starter = MongodStarter.getDefaultInstance();
            IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();

            mongodExecutable = starter.prepare(mongodConfig);
            MongodProcess mongoProcess = mongodExecutable.start();
            MongoClient mongoClient = new MongoClient("localhost", port);
            db = mongoClient.getDatabase("smartSocietyLocalMongoDB");
        } catch (IOException ex) {
            close();
            throw ex;
        }
    }

    public void close() {
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }

    public MongoDatabase getMongoDb() {
        return db;
    }

    public static MongoRunner withPort(int port) throws IOException {
        return new MongoRunner(port);
    }

}
