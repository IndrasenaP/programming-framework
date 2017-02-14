/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario1;

import at.ac.tuwien.dsg.GooglePeerFactory;
import at.ac.tuwien.dsg.SoftwarePeer;
import at.ac.tuwien.dsg.smartcom.adapters.RESTInputAdapter;
import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.MongoClient;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.CollectiveKind;
import eu.smartsocietyproject.pf.CollectiveKindRegistry;
import eu.smartsocietyproject.pf.MongoRunner;
import eu.smartsocietyproject.pf.PeerManagerMongoProxy;
import eu.smartsocietyproject.pf.SmartSocietyApplicationContext;
import eu.smartsocietyproject.pf.TaskDefinition;
import eu.smartsocietyproject.pf.helper.InternalPeerManager;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;
import eu.smartsocietyproject.runtime.Runtime;
import eu.smartsocietyproject.scenario1.helper.RQATaskDefinition;
import eu.smartsocietyproject.scenario2.helper.GreenMail.RunMailServer;
import eu.smartsocietyproject.scenario2.helper.GreenMailOutputAdapter;
import eu.smartsocietyproject.scenario2.helper.PeerLoader;
import eu.smartsocietyproject.smartcom.PeerChannelAddressAdapter;
import eu.smartsocietyproject.smartcom.SmartComServiceImpl;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class Scenario1 implements NotificationCallback {

    private static Runtime runtime;
    private static final boolean DEMO = true;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, CommunicationException {
        
        if (DEMO) {
            RunMailServer.start();
        }
        
        CollectiveKindRegistry kindRegistry = CollectiveKindRegistry
                .builder().register(CollectiveKind.EMPTY).build();
        
        SoftwarePeer googlePeer = GooglePeerFactory.startPeer(Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream("GooglePeer.properties"));

        MongoRunner runner = MongoRunner.withPort(6666);
        PeerManagerMongoProxy.Factory pmFactory
                = PeerManagerMongoProxy.factory(runner.getMongoDb());

        MongoClient client = new MongoClient("localhost", 6666);

        SmartSocietyApplicationContext context
                = new SmartSocietyApplicationContext(kindRegistry,
                        pmFactory,
                        new SmartComServiceImpl.Factory(client));

        SmartComServiceImpl smartCom = (SmartComServiceImpl) context.getSmartCom();
        
        if (DEMO) {
            smartCom.getCommunication()
                    .removeOutputAdapter(Identifier.adapter("Email"));
            smartCom.getCommunication()
                    .registerOutputAdapter(GreenMailOutputAdapter.class);
        }

        //todo-sv: if the platform is responsible for setting up and handling
        //smartcom correctly how does the platform know what input adapters
        //to initiate? for example the google SW peer has a PeerChannelAddress
        //which allows us reaching it however it responds to one hard coded
        //url and we have to set up an listening input adapter on this url
        //but this adapter itself does not have a peer to which it belongs
        //how can we make the smartcom service know on whoch channels
        //to expect responses?
        
        PeerLoader.laodPeers(context);

        smartCom.registerNotificationCallback(new Scenario1());
        Properties props = new Properties();
        props.load(Scenario1.class.getClassLoader()
                .getResourceAsStream("EmailAdapter.properties"));
        //todo-sv: for adapter discussion: for example, how will we find 
        //and start up this adapters? since they belong to the application
        //but have no concrete peer
        smartCom.addEmailPullAdapter("RQA", props);
        smartCom.getCommunication()
                .addPushAdapter(new RESTInputAdapter(9696, "searchResult"));

        Scenario1.runtime = new Runtime(context, new RQAApplication(context));
        Scenario1.runtime.run();
    }

    public void notify(Message message) {
        if (message.getConversationId().equals("RQA")) {            
            ObjectNode rqa = JsonNodeFactory.instance.objectNode();
            rqa.set("question", JsonNodeFactory.instance
                    .textNode(message.getContent().trim()));            
            Scenario1.runtime.startTask(new RQATaskDefinition(rqa, message.getSenderId()));
        }
    }

}
