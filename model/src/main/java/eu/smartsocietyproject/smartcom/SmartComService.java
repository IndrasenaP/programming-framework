/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

//import at.ac.tuwien.dsg.smartcom.model.Message;

import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Message;
import eu.smartsocietyproject.peermanager.PeerManager;


/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public interface SmartComService {

    void send(Message msg) throws CommunicationException;

    //todo-sv: define interface
//    void send(Message m);
    public interface Factory {
        public SmartComService create(PeerManager pm);
    }
}
