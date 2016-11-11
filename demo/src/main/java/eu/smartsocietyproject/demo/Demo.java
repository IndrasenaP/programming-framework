/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.demo;

import at.ac.tuwien.dsg.smartcom.adapters.RESTInputAdapter;
import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.MongoClient;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.CBTBuilder;
import eu.smartsocietyproject.pf.CollectiveKind;
import eu.smartsocietyproject.pf.CollectiveKindRegistry;
import eu.smartsocietyproject.pf.MongoRunner;
import eu.smartsocietyproject.pf.PeerManagerMongoProxy;
import eu.smartsocietyproject.pf.SmartSocietyApplicationContext;
import eu.smartsocietyproject.pf.TaskDefinition;
import eu.smartsocietyproject.pf.TaskFlowDefinition;
import eu.smartsocietyproject.pf.helper.InternalPeerManager;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;
import eu.smartsocietyproject.runtime.Runtime;
import eu.smartsocietyproject.smartcom.PeerChannelAddressAdapter;
import eu.smartsocietyproject.smartcom.SmartComService;
import eu.smartsocietyproject.smartcom.SmartComServiceImpl;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class Demo implements NotificationCallback {

    private static Runtime runtime;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, CommunicationException {
        CollectiveKindRegistry kindRegistry = CollectiveKindRegistry
                .builder().register(CollectiveKind.EMPTY).build();

        MongoRunner runner = MongoRunner.withPort(6666);
        PeerManagerMongoProxy.Factory pmFactory
                = PeerManagerMongoProxy.factory(runner.getMongoDb());

        MongoClient client = new MongoClient("localhost", 6666);

        SmartSocietyApplicationContext context
                = new SmartSocietyApplicationContext(kindRegistry,
                        pmFactory,
                        new SmartComServiceImpl.Factory(client));

        SmartComServiceImpl smartCom = (SmartComServiceImpl) context.getSmartCom();

        InternalPeerManager pm = (InternalPeerManager) context.getPeerManager();

        //todo-sv: if the platform is responsible for setting up and handling
        //smartcom correctly how does the platform know what input adapters
        //to initiate? for example the google SW peer has a PeerChannelAddress
        //which allows us reaching it however it responds to one hard coded
        //url and we have to set up an listening input adapter on this url
        //but this adapter itself does not have a peer to which it belongs
        //how can we make the smartcom service know on whoch channels
        //to expect responses?
        pm.persistPeer(PeerIntermediary
                .builder("google", "SWPeerForSearch")
                .addDeliveryAddress(PeerChannelAddressAdapter
                        .convert(new PeerChannelAddress(
                                Identifier.peer("google"),
                                Identifier.channelType("REST"),
                                Arrays.asList("localhost:8000/"))
                        )
                )
                .addAttribute("restaurantQA", AttributeType.from("true"))
                .build());

        pm.persistPeer(PeerIntermediary
                .builder("expert", "HumanExpert")
                .addDeliveryAddress(PeerChannelAddressAdapter
                        .convert(new PeerChannelAddress(
                                Identifier.peer("expert"),
                                Identifier.channelType("Email"),
                                Arrays.asList("s.videnov@dsg.tuwien.ac.at"))
                        )
                )
                .addAttribute("restaurantQA", AttributeType.from("true"))
                .build());

        smartCom.registerNotificationCallback(new Demo());
        Properties props = new Properties();
        props.load(Demo.class.getClassLoader()
                .getResourceAsStream("EmailAdapter.properties"));
        //todo-sv: for adapter discussion: for example, how will we find 
        //and start up this adapters? since they belong to the application
        //but have no concrete peer
        smartCom.addEmailPullAdapter("RQA", props);
        smartCom.getCommunication()
                .addPushAdapter(new RESTInputAdapter(9696, "searchResult"));

        Demo.runtime = new Runtime(context, new DemoApplication(context));
        Demo.runtime.run();
    }

    public void notify(Message message) {
        if (message.getConversationId().equals("RQA")) {
            TaskDefinition task = new TaskDefinition(JsonNodeFactory.instance
                    .textNode(message.getContent()));
            Demo.runtime.startTask(task);
        }
    }

}
