/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.demo;

import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.MongoClient;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveKind;
import eu.smartsocietyproject.pf.CollectiveKindRegistry;
import eu.smartsocietyproject.pf.MongoRunner;
import eu.smartsocietyproject.pf.PeerManagerMongoProxy;
import eu.smartsocietyproject.pf.SmartSocietyApplicationContext;
import eu.smartsocietyproject.pf.TaskDefinition;
import eu.smartsocietyproject.pf.helper.InternalPeerManager;
import eu.smartsocietyproject.runtime.Runtime;
import eu.smartsocietyproject.smartcom.SmartComService;
import java.io.IOException;
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
        PeerManagerMongoProxy.Factory pmFactory = 
                PeerManagerMongoProxy.factory(runner.getMongoDb());
        
        ApplicationContext context = 
                new SmartSocietyApplicationContext(kindRegistry, pmFactory);
        
        MongoClient client = new MongoClient("localhost", 6666);
        
        SmartComService smartCom = new SmartComService(
                (InternalPeerManager)context.getPeerManager(), 
                client);
        smartCom.registerNotificationCallback(new Demo());
        Properties props = new Properties();
        props.load(Demo.class.getClassLoader()
                .getResourceAsStream("EmailAdapter.properties"));
        smartCom.addEmailPullAdapter("question", props);
        
        Demo.runtime = new Runtime(context, new DemoApplication());
        Demo.runtime.run();
    }

    public void notify(Message message) {
        String msg = message.getContent().split("QuestionEnd")[0];
        //System.out.println("This would go to google: " + msg);
        TaskDefinition task = new TaskDefinition(JsonNodeFactory.instance
                .textNode(msg));
        Demo.runtime.startTask(task);
    }
    
}
