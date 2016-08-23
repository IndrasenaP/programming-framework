/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public abstract class EntityCore {
    protected ObjectMapper mapper = new ObjectMapper();
    protected JsonNode root;
    
    protected EntityCore(String defaultJson) {
        parseThis(defaultJson);
    }
    
    public String toJson(){
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(EntityCore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    protected void parseThis(String json) {
        try {
            this.root = mapper.readTree(json);
        } catch (IOException ex) {
            Logger.getLogger(EntityCore.class.getName()).log(Level.SEVERE, null, ex);
            try {
                this.root = mapper.readTree("{}");
            } catch (IOException ex1) {
                Logger.getLogger(EntityCore.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
    
    protected void setNode(JsonNode node) {
        if(node.isMissingNode()) {
            return;
        }
        
        this.root = node;
    }
}
