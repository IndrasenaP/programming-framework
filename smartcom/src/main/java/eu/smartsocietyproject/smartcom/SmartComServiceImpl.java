/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import at.ac.tuwien.dsg.smartcom.Communication;
import at.ac.tuwien.dsg.smartcom.SmartCom;
import at.ac.tuwien.dsg.smartcom.SmartComBuilder;
import at.ac.tuwien.dsg.smartcom.adapter.InputPullAdapter;
import at.ac.tuwien.dsg.smartcom.adapters.EmailInputAdapter;
import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import at.ac.tuwien.dsg.smartcom.model.MessageLogLevel;
import at.ac.tuwien.dsg.smartcom.utils.MongoDBInstance;
import at.ac.tuwien.dsg.smartcom.utils.PropertiesLoader;
import com.mongodb.MongoClient;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.pf.helper.InternalPeerManager;
import eu.smartsocietyproject.smartcom.adapter.EmailInputAdapterWithPeerSender;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Notification;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class SmartComServiceImpl implements SmartComService {

	private final SmartComPeerManager peerManager;
	private final SmartCom smartCom;
	private final Communication communication;

	//todo-sv: consider passing in applicationContext and retrieving PM from CTX
	public SmartComServiceImpl(InternalPeerManager peerManager,
			MongoClient mongoClient) throws CommunicationException {
		this.peerManager = new SmartComPeerManager(peerManager);
		this.smartCom = new SmartComBuilder(this.peerManager, this.peerManager, this.peerManager)
				.setMongoClient(mongoClient)
				.setMessageLogLevel(MessageLogLevel.ALL)
				.create();
		this.communication = smartCom.getCommunication();
	}
    
    //todo-sv: discuss wit ogi how exactly to init this
    public void addEmailPullAdapter(String conversationId, Properties props) {
        this.communication.addPullAdapter(
				new EmailInputAdapterWithPeerSender(conversationId,
                        props.getProperty("hostIncoming"),
                        props.getProperty("username"),
                        props.getProperty("password"),
						Integer.valueOf(props.getProperty("portIncoming")), 
						true, "test", "test", true, 
                        peerManager.getPeerManager()), 1000);
    }
    
    //todo-sv: only temporary until decided how default input response adapters
    //will be set up by SM-Service automatically
    public Communication getCommunication() {
        return this.communication;
    }

	public void send(Message msg) throws CommunicationException {
        //todo: fix nullpointer?!
		this.communication.send(msg);
	}
	
	public Identifier registerNotificationCallback(NotificationCallback callback) throws CommunicationException {
		return this.communication.registerNotificationCallback(callback);
	}
    
    public void unregisterNotificationCallback(Identifier callback) throws CommunicationException {
        this.communication.unregisterNotificationCallback(callback);
    }

	public void shutdown() throws CommunicationException {
		this.smartCom.tearDownSmartCom();
	}
    
    public static Factory factory(MongoClient client) {
        return new Factory(client);
    }
    
    public static class Factory implements SmartComService.Factory {
        private MongoClient client;
        
        public Factory(MongoClient client) {
            this.client = client;
        }
        
        @Override
        public SmartComServiceImpl create(PeerManager pm) {
            try {
                //todo-sv: add error handling for cast
                return new SmartComServiceImpl((InternalPeerManager)pm, client);
            } catch (CommunicationException ex) {
                //todo-sv: consider propagating the exception instead
                throw new IllegalStateException(ex);
            }
        } 
    }
}
