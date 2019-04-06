import com.mongodb.MongoClient;
import com.typesafe.config.*;
import eu.smartsocietyproject.payment.PaymentService;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.pf.MongoRunner;
import eu.smartsocietyproject.pf.PeerManagerMongoProxy;
import eu.smartsocietyproject.runtime.Runtime;
import eu.smartsocietyproject.runtime.SmartSocietyComponents;
import eu.smartsocietyproject.smartcom.SmartComService;
import eu.smartsocietyproject.smartcom.SmartComServiceImpl;
import org.web3j.crypto.Credentials;
import org.web3j.tx.gas.ContractGasProvider;
import service.PaymentServiceImpl;


import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        Config config = getConfig(args);
        try (MongoRunner runner = getMongoInstance(config)) {
            SmartSocietyComponents components =
                new SmartSocietyComponents(getPeerManagerFactory(config, runner),
                            getSmartComServiceFactory(getMongoClientInstance(config)), getPaymentService(config));

                Runtime.fromApplication(config, components);
        } catch (IOException | InstantiationException e) {
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

    private static PaymentService getPaymentService(Config config){

        String node = config.getString("ethereum.node");
        BigInteger getCollectiveBasedTasksGasPrice = BigInteger.valueOf(config.getLong("getCollectiveBasedTasks.gasPrice"));
        BigInteger createCollectiveBasedTaskGasPrice = BigInteger.valueOf(config.getLong("createCollectiveBasedTask.gasPrice"));
        BigInteger defaultGasPrice = BigInteger.valueOf(config.getLong("default.gasPrice"));
        BigInteger getCollectiveBasedTasksGasLimit = BigInteger.valueOf(config.getLong("getCollectiveBasedTasks.gasLimit"));
        BigInteger createCollectiveBasedTaskGasLimit = BigInteger.valueOf(config.getLong("createCollectiveBasedTask.gasLimit"));
        BigInteger defaultGasLimit = BigInteger.valueOf(config.getLong("default.gasLimit"));
        String privateKey = config.getString("ethereum.privateKey");
        BigInteger minimumPayment = BigInteger.valueOf(config.getLong("minimum.payment"));

        ContractGasProvider gasProvider = new ContractGasProvider() {
            @Override
            public BigInteger getGasPrice(String contractFunc) {
                switch (contractFunc){
                    case "getCollectiveBasedTasks":
                        return getCollectiveBasedTasksGasPrice;
                    case "createCollectiveBasedTask":
                        return createCollectiveBasedTaskGasPrice;
                    default:
                        return defaultGasPrice;
                }
            }

            @Override
            public BigInteger getGasPrice() {
                return defaultGasPrice;
            }

            @Override
            public BigInteger getGasLimit(String contractFunc) {
                switch (contractFunc){
                    case "getCollectiveBasedTasks":
                        return getCollectiveBasedTasksGasLimit;
                    case "createCollectiveBasedTask":
                        return createCollectiveBasedTaskGasLimit;
                    default:
                        return defaultGasLimit;
                }
            }

            @Override
            public BigInteger getGasLimit() {
                return defaultGasLimit;
            }
        };
        Credentials credentials = Credentials.create(privateKey);

        return new PaymentServiceImpl(node, gasProvider, credentials, minimumPayment);
    }
}
