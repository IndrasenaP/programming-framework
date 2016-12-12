package eu.smartsocietyproject.pf;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.runtime.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

public class MongoRunner implements AutoCloseable {
    private final MongodExecutable mongodExecutable;
    private final MongoDatabase db;
    private final Logger logger = LoggerFactory.getLogger("MONGODRUNNER");
    private MongoRunner(int port) throws IOException {
        try {
            IRuntimeConfig runtimeConfig =
                new RuntimeConfigBuilder()
                    .defaultsWithLogger(Command.MongoD, logger)
                    .build();
            MongodStarter starter = MongodStarter.getInstance(runtimeConfig);
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
