import com.mongodb.MongoClient;
import com.typesafe.config.*;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.pf.MongoRunner;
import eu.smartsocietyproject.pf.PeerManagerMongoProxy;
import eu.smartsocietyproject.runtime.Runtime;
import eu.smartsocietyproject.runtime.SmartSocietyComponents;
import eu.smartsocietyproject.smartcom.SmartComService;
import eu.smartsocietyproject.smartcom.SmartComServiceImpl;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Config config = getConfig(args);
        try (MongoRunner runner = getMongoInstance(config)) {
            SmartSocietyComponents components =
                new SmartSocietyComponents(getPeerManagerFactory(config, runner),
                            getSmartComServiceFactory(getMongoClientInstance(config)));
            Runtime runtime =
                Runtime.fromApplication(config, components);
            runtime.run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static Config getConfig(String[] args) {
        File configFile = new File(args[0]);
        return ConfigFactory.parseFileAnySyntax(configFile);
    }

    private static MongoRunner getMongoInstance(Config config) throws IOException {
        return MongoRunner.withPort(6666);
    }
    
    private static MongoClient getMongoClientInstance(Config config) {
        return new MongoClient("localhost", 6666);
    }

    /**
     * @param config
     * @param mongoInstance used only for the PeerManagerMongoProxy if the WP4 peer manager is not configured
     * @return
     */
    private static PeerManager.Factory getPeerManagerFactory(Config config, MongoRunner mongoInstance) {
        String c = config.getString("unitn.uri"); /* TODO find a proper name for the property */
        if ( c!= null && !c.isEmpty()) {
            /* TODO return PeerManagerProxy.fatory(c); */
        }
        return PeerManagerMongoProxy.factory(mongoInstance.getMongoDb());
    }
    
    private static SmartComService.Factory getSmartComServiceFactory(MongoClient client) {
        return SmartComServiceImpl.factory(client);
    }
}
