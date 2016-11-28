package eu.smartsocietyproject.scenario4;

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

public class Scenario4 implements NotificationCallback {
    private static Logger logger = LoggerFactory.getLogger(Scenario4.class);
    private static Runtime runtime;
    private static ObjectMapper mapper = new ObjectMapper();
    private static InternalPeerManager pm;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, CommunicationException, InstantiationException {
        MongoRunner runner = MongoRunner.withPort(6666);
        PeerManagerMongoProxy.Factory pmFactory
            = PeerManagerMongoProxy.factory(runner.getMongoDb());

        MongoClient client = new MongoClient("localhost", 6666);


        SmartSocietyComponents components =
            new SmartSocietyComponents(
                PeerManagerMongoProxy.factory(runner.getMongoDb()),
                new SmartComS4Factory(client, 9697)
            );

        logger.info("Creating Runtime");
        Scenario4.runtime = Runtime.fromApplication(ConfigFactory.empty(), components, S4Application.class);

        logger.info("Starting Runtime");
        Scenario4.runtime.run();
    }

    private void createPeer(Integer gitlab_id, String skill) {
        pm.persistPeer(PeerIntermediary.builder(gitlab_id.toString(), "developer")
                                       .addAttribute("gitlab_id", AttributeType.from(gitlab_id))
                                       .addAttribute("skill", AttributeType.from(skill))
                                       .build());
    }

    private void createPeer(String messageContent) {
        try {
            JsonNode node = mapper.readTree(messageContent);
            Optional<Integer> id =
                Optional.ofNullable(node.get("id")).map(jsonNode -> jsonNode.asInt());
            Optional<String> skill =
                Optional.ofNullable(node.get("skill")).map(jsonNode -> jsonNode.asText());
            if (!id.isPresent() || id.get() == 0 || !skill.isPresent()) {
                logger.warn(
                    String.format(
                        "Request with missing fields (id must be an integer, the Gitlab id): %s",
                        messageContent));
                return;
            }
            createPeer(id.get(), skill.get());
            logger.info(String.format("Created User %d with skill %s", id.get(), skill.get()));
        } catch (IOException e) {
            logger.warn(String.format("Request with wrong format: %s", messageContent));
            return;
        }
    }

    public void notify(Message message) {
        if (message.getConversationId().equals("Scenario4")) {
            switch (message.getType()) {
                case "submit":
                    runS4Task(message);
                    break;
                case "peer":
                    createPeer(message.getContent());
                    break;
                default:
                    logger.error("Unknown request type: " + message.getType());
            }

        }
    }

    private void runS4Task(Message message) {
        try {
            TaskDefinition task = new TaskDefinition(mapper.readTree(message.getContent()));
            logger.info(String.format("Preparing to start task:  %s", task.getJson().toString()));
            Scenario4.runtime.startTask(task);
        } catch (IOException e) {
            logger.error("Unable to accept request");
        }
    }

    private static class SmartComS4Factory extends SmartComServiceImpl.Factory {

        private final int restAdapterPort;

        public SmartComS4Factory(MongoClient client, int restAdapterPort) {
            super(client);
            this.restAdapterPort = restAdapterPort;
        }

        @Override
        public SmartComServiceImpl create(PeerManager pm) {
            SmartComServiceImpl sc = super.create(pm);
            try {
                sc.registerNotificationCallback(new Scenario4());
                sc.getCommunication().addPushAdapter(new RESTInputAdapter(restAdapterPort, ""));
            } catch (CommunicationException ex) {
                //todo-sv: consider propagating the exception instead
                throw new IllegalStateException(ex);
            }
            return sc;
        }

    }
}

