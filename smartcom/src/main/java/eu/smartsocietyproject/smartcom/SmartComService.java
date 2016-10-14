/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import at.ac.tuwien.dsg.smartcom.Communication;
import at.ac.tuwien.dsg.smartcom.SmartCom;
import at.ac.tuwien.dsg.smartcom.SmartComBuilder;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import at.ac.tuwien.dsg.smartcom.model.MessageLogLevel;
import eu.smartsocietyproject.pf.helper.InternalPeerManager;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class SmartComService {

    private SmartComPeerManager peerManager;
    private SmartCom smartCom;
    private Communication communication;

    //todo: consider passing in applicationContext and retrieving PM from CTX
    public SmartComService(InternalPeerManager peerManager) {
        this.peerManager = new SmartComPeerManager(peerManager);
    }

    public void init() throws CommunicationException {
        this.smartCom
                = new SmartComBuilder(peerManager, peerManager, peerManager)
                        .setMessageLogLevel(MessageLogLevel.ALL)
                        .create();

        this.communication = smartCom.getCommunication();

        Identifier peerId = Identifier.peer("sveti");
        Message.MessageBuilder builder
                = new Message.MessageBuilder()
                        .setType("TASK")
                        .setSubtype("REQUEST")
                        .setReceiverId(peerId)
                        .setSenderId(Identifier.component("DEMO"))
                        .setConversationId("firstTest")
                        .setContent("Hello World!");
        Message msg = builder.create();

        communication.send(msg);
    }

    public void shutdown() throws CommunicationException {
        if (smartCom != null) {
            this.smartCom.tearDownSmartCom();
        }
    }
}
