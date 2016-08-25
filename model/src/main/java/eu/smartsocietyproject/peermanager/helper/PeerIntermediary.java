/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * PeerIntermediary is a DTO class which allows the local PM proxy to 
 * operate.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerIntermediary extends EntityHandler {
    
    protected PeerIntermediary() {
        super("id");
    }
    
    public static PeerIntermediary createEmpty(String id) {
        PeerIntermediary peer = new PeerIntermediary();
        peer.setId(id);
        return peer;
    }
    
    public static PeerIntermediary createFromJson(String json) {
        PeerIntermediary peer = new PeerIntermediary();
        peer.parseThis(json);
        //todo-sv: check eventually if there is an id?
        return peer;
    }
    
    protected static PeerIntermediary createFromJson(JsonNode node) {
        PeerIntermediary peer = new PeerIntermediary();
        peer.root = node;
        return peer;
    }
}
