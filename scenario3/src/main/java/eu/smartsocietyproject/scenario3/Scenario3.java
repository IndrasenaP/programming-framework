package eu.smartsocietyproject.scenario3;

import at.ac.tuwien.dsg.smartcom.adapters.RESTInputAdapter;
import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.helper.InternalPeerManager;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;
import eu.smartsocietyproject.runtime.Runtime;
import eu.smartsocietyproject.runtime.SmartSocietyComponents;
import eu.smartsocietyproject.smartcom.SmartComService;
import eu.smartsocietyproject.smartcom.SmartComServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class Scenario3 implements NotificationCallback {
    private static Logger logger = LoggerFactory.getLogger(Scenario3.class);
    private static Runtime runtime;
    private static ObjectMapper mapper = new ObjectMapper();
    private static InternalPeerManager pm;

    public static void main() throws IOException, CommunicationException, InstantiationException {
        MongoRunner runner = MongoRunner.withPort(6666);
        String pmUri = System.getenv("PM_URI");
        String omUri = System.getenv("OM_URI");

        if ( omUri == null ) {
            System.err.println("You need to set the PM_URI environment variable with the " +
                               "address of the Peer Manager that the orchestrator is going to use");
            System.exit(-1);
        }

        if ( pmUri == null ) {
            System.err.println("You need to set the PM_URI environment variable with the " +
                               "address of the Peer Manager that the orchestrator is going to use");
            System.exit(-1);
        }
        PeerManager.Factory pmFactory
            = new PeerManager.Factory() {
            @Override
            public PeerManager create(ApplicationContext context) {
                throw new UnsupportedOperationException("TODO");
            }
        };

        MongoClient client = new MongoClient("localhost", 6666);

        SmartSocietyComponents components =
            new SmartSocietyComponents(
                pmFactory,
                new SmartComS3Factory(client, 9697)
            );

        logger.info("Creating Runtime");
        Scenario3.runtime = Runtime.fromApplication(ConfigFactory.empty(), components, S3Application.class);

        pm = (InternalPeerManager)Scenario3.runtime.getContext().getPeerManager();

        logger.info("Starting Runtime");
        Scenario3.runtime.run();
    }

    public void notify(Message message) {
        if (message.getConversationId().equals("Scenario3")) {
            switch (message.getType()) {
                case "submit":
                    runS3Task(message);
                    break;
                default:
                    logger.error("Unknown request type: " + message.getType());
            }

        }
    }

    private void runS3Task(Message message) {
        try {
            TaskDefinition task = new TaskDefinition(mapper.readTree(message.getContent()));
            logger.info(String.format("Preparing to start task:  %s", task.getJson().toString()));
            Scenario3.runtime.startTask(task);
        } catch (IOException e) {
            logger.error("Unable to accept request");
        }
    }

    private static class SmartComS3Factory extends SmartComServiceImpl.Factory {

        private final int restAdapterPort;

        public SmartComS3Factory(MongoClient client, int restAdapterPort) {
            super(client);
            this.restAdapterPort = restAdapterPort;
        }

        @Override
        public SmartComServiceImpl create(PeerManager pm) {
            SmartComServiceImpl sc = super.create(pm);
            try {
                sc.registerNotificationCallback(new Scenario3());
                sc.getCommunication().addPushAdapter(new RESTInputAdapter(restAdapterPort, ""));
            } catch (CommunicationException ex) {
                //todo-sv: consider propagating the exception instead
                throw new IllegalStateException(ex);
            }
            return sc;
        }

    }
}

