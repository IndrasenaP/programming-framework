/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.pf.attributes.StringAttribute;

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
    
    public String getId() {
        return StringAttribute
                .createFromJson(this.getAttribute(this.keyId))
                .getValue();
    }
    
    public static PeerIntermediary createFromJson(String json) {
        return new PeerIntermediary(json);
    }
}
