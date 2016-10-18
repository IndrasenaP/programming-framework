/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import at.ac.tuwien.dsg.smartcom.Communication;
import at.ac.tuwien.dsg.smartcom.SmartCom;
import at.ac.tuwien.dsg.smartcom.SmartComBuilder;
import at.ac.tuwien.dsg.smartcom.adapters.EmailInputAdapter;
import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import at.ac.tuwien.dsg.smartcom.model.MessageLogLevel;
import at.ac.tuwien.dsg.smartcom.utils.MongoDBInstance;
import at.ac.tuwien.dsg.smartcom.utils.PropertiesLoader;
import com.mongodb.MongoClient;
import eu.smartsocietyproject.pf.helper.InternalPeerManager;
import javax.management.Notification;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class SmartComService {

	private final SmartComPeerManager peerManager;
	private final SmartCom smartCom;
	private final Communication communication;

	//todo: consider passing in applicationContext and retrieving PM from CTX
	public SmartComService(InternalPeerManager peerManager,
			MongoClient mongoClient) throws CommunicationException {
		this.peerManager = new SmartComPeerManager(peerManager);
		this.smartCom = new SmartComBuilder(this.peerManager, this.peerManager, this.peerManager)
				.setMongoClient(mongoClient)
				.setMessageLogLevel(MessageLogLevel.ALL)
				.create();
		this.communication = smartCom.getCommunication();
	}

	public void send(Message msg) throws CommunicationException {
		String id = msg.getConversationId();
		this.communication.addPullAdapter(
				new EmailInputAdapter(msg.getConversationId(),
						PropertiesLoader.getProperty("EmailAdapter.properties", "hostIncoming"),
                        PropertiesLoader.getProperty("EmailAdapter.properties", "username"),
                        PropertiesLoader.getProperty("EmailAdapter.properties", "password"),
						Integer.valueOf(PropertiesLoader.getProperty("EmailAdapter.properties", "portIncoming")), 
						true, "test", "test", true), 1000);
		this.communication.send(msg);
	}
	
	public void registerNotificationCallback(NotificationCallback callback) throws CommunicationException {
		this.communication.registerNotificationCallback(callback);
	}

	public void shutdown() throws CommunicationException {
		this.smartCom.tearDownSmartCom();
	}
}
