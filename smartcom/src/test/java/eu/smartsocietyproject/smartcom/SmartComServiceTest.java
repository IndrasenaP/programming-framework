/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import at.ac.tuwien.dsg.smartcom.adapters.EmailInputAdapter;
import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import at.ac.tuwien.dsg.smartcom.utils.MongoDBInstance;
import at.ac.tuwien.dsg.smartcom.utils.PropertiesLoader;
import com.mongodb.MongoClient;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.CBTBuilder;
import eu.smartsocietyproject.pf.CollectiveKindRegistry;
import eu.smartsocietyproject.pf.MongoRunner;
import eu.smartsocietyproject.pf.PeerManagerMongoProxy;
import eu.smartsocietyproject.pf.helper.EntityHandler;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class SmartComServiceTest implements NotificationCallback {
    
    private static final ApplicationContext context = new ApplicationContext() {
        @Override
        public UUID getId() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public CollectiveKindRegistry getKindRegistry() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public CBTBuilder registerBuilderForCBTType(String type, CBTBuilder builder) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public CBTBuilder getCBTBuilder(String type) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public PeerManager getPeerManager() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    private MongoRunner runner;
    private PeerManagerMongoProxy pm;
    private SmartComService scs;
	private boolean response;
    
    public SmartComServiceTest() {
    }
    
    @Before
    public void setUp() throws Exception {
		this.response = false;
        runner = MongoRunner.withPort(6666);
        pm = PeerManagerMongoProxy.factory(runner.getMongoDb())
                .create(context);
		MongoClient client = new MongoClient("localhost", 6666);
        scs = new SmartComService(pm, client);
        scs.registerNotificationCallback(this);
        
        PeerIntermediary.Builder peer = PeerIntermediary
                .builder("sveti", "defaultRole")
                .addDeliveryAddress(PeerChannelAddressAdapter
                    .convert(new PeerChannelAddress(
                        Identifier.peer("sveti"),
                        Identifier.channelType("Email"), 
                        Arrays.asList("s.videnov@dsg.tuwien.ac.at"))
                    )
                );
        
        pm.persistPeer(PeerIntermediary.create(peer.build()));
    }
    
    @After
    public void tearDown() {
        try {
            scs.shutdown();
        } catch (Exception ex) {
            Logger.getLogger(SmartComServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        runner.close();
    }

    //@Test //this is a very simple manual test. It requieres user interaction!
    public void testSend() throws Exception {
        String id = "firstTest - " + UUID.randomUUID();
        Properties props = new Properties();
        props.load(this.getClass()
                .getClassLoader()
                .getResourceAsStream("EmailAdapter.properties"));
        
        scs.addEmailPullAdapter(id, props);
        
		Message.MessageBuilder builder
				= new Message.MessageBuilder()
						.setType("TASK")
						.setSubtype("REQUEST")
						.setReceiverId(Identifier.peer("sveti"))
						.setSenderId(Identifier.component("SCSTest"))
						.setConversationId(id)
						.setContent("Hello World!");
        
        scs.send(builder.create());
		while(!response) {
			Thread.sleep(1000);
		}
    }

	@Override
	public void notify(Message message) {
		this.response = true;
	}
    
}
