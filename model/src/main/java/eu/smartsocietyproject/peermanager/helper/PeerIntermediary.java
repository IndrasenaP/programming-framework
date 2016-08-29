/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

/**
 * PeerIntermediary is a DTO class which allows the local PM proxy to 
 * operate.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerIntermediary extends EntityHandler {
    
    private String keyId = "id";
    
    protected PeerIntermediary(String json) {
        super(json);
    }
    
    protected PeerIntermediary(EntityHandler handler) {
        super(handler.root);
    }
    
    public String getId() {
        return this.getAttribute(this.keyId);
    }
    
    public static PeerIntermediary createFromJson(String json) {
        return new PeerIntermediary(json);
    }
    
    public static PeerIntermediary create(EntityHandler attributes) {
        return new PeerIntermediary(attributes);
    }
}
