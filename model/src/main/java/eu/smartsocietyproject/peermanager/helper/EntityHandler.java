/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.attributes.StringAttribute;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class EntityHandler extends EntityCore {
    
    protected final String keyId;

    protected EntityHandler() {
        this("");
    }
    
    protected EntityHandler(String keyId) {
        super("{}");
        this.keyId = keyId;
    }
    
    protected void setId(String id) {
        this.addAttributeValue(this.keyId, StringAttribute.create(id));
    }
    
    public String getId() {
        return this.getAttributeValue(keyId, StringAttribute.create()).getValue();
    }
    
    protected <T extends Attribute> T getAttributeValue(String name, T attribute) {
        try {
            attribute.parseJson(mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(root.path(name)));
        } catch (JsonProcessingException ex) {
            Logger.getLogger(EntityCore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return attribute;
    }
    
    protected <T extends EntityCore> T getAttributeNode(String name, T attribute) {
        attribute.setNode(root.path(name));
        return attribute;
    }
    
    public <T extends Attribute> T getAttribute(String name, T attribute) {
        if(this.keyId.equals(name)) {
            return attribute;
        }
        
        return this.getAttributeValue(name, attribute);
    }
    
    protected <T extends EntityCore> T addAttributeNode(String name, T attribute) {
        if(this.root.isObject()) {
            ((ObjectNode)root).set(name, attribute.root);
        }
        
        return attribute;
    }
    
    protected void addAttributeValue(String name, Attribute attribute) {
        //todo-sv:add exception for error handling
        if(this.root.isObject()) {
            try {
                JsonNode node = mapper.readTree(attribute.toJson());
                ((ObjectNode)root).set(name, node);
            } catch (IOException ex) {
                Logger.getLogger(EntityCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void addAttribute(String name, Attribute attribute) {
        if(this.keyId.equals(name)) {
           return; 
        }
        
        addAttributeValue(name, attribute);
    }
    
    public static EntityHandler create() {
        return new EntityHandler();
    }
    
    public static EntityHandler create(String json) {
        EntityHandler handler = new EntityHandler();
        handler.parseThis(json);
        return handler;
    }
}
