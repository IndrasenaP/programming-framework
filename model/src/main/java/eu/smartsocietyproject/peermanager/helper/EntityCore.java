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
 * This class is the base for every object which is represented by 
 * an arbitrary JSON.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public abstract class EntityCore {
    protected ObjectMapper mapper = new ObjectMapper();
    protected JsonNode root;
    
    protected EntityCore(String defaultJson) {
        parseThis(defaultJson);
    }
    
    /**
     * Will convert the object stored in the underlying JSON structure 
     * into a properly formated JSON-String.
     * 
     * @return - object as JSON-String
     */
    public String toJson(){
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(EntityCore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    /**
     * This function performs a default mapping between a JSON-String and a
     * {@link JsonNode}.
     * 
     * @param json - a JSON-String
     */
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
    
    /**
     * Allows for setting the root node directly in case it exists already.
     * 
     * The node will only be set if {@link JsonNode#isMissingNode()} does not
     * return true.
     * 
     * @param node - The {@link JsonNode}
     */
    protected void setNode(JsonNode node) {
        if(node.isMissingNode()) {
            return;
        }
        
        this.root = node;
    }
}
