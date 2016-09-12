/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.helper;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.BasicAttribute;

/**
 * PeerIntermediary is a DTO class which allows the local PM proxy to 
 * operate.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerIntermediary {
    
    private String keyId = "id";
    private final BasicAttribute<String> id;
    private EntityHandler entity;
    
    protected PeerIntermediary(EntityHandler handler) throws PeerManagerException {
        this.entity = handler;
        this.id = this.entity.getAttribute(keyId, AttributeType.STRING);
    }
    
    public String getId() {
        return this.id.getValue();
    }
    
    public JsonNode toJson() {
        return this.entity.toJson();
    }
    
    public static PeerIntermediary createFromJson(String json) throws PeerManagerException {
        return new PeerIntermediary(EntityHandler.create(json));
    }
    
    public static PeerIntermediary create(EntityHandler attributes) throws PeerManagerException {
        return new PeerIntermediary(attributes);
    }
}
