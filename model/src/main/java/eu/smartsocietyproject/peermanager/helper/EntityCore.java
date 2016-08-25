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
    protected static ObjectMapper mapper = new ObjectMapper();
    protected final JsonNode root;
    
    protected EntityCore(String json) {
        this.root = EntityCore.parseJson(json);
    }
    
    protected EntityCore(JsonNode root) {
        this.root = root;
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
    protected static JsonNode parseJson(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException ex) {
            Logger.getLogger(EntityCore.class.getName()).log(Level.SEVERE, null, ex);
           //not needed since adding is not possible anymore so empty entities are useless
//            try {
//                return mapper.readTree("{}");
//            } catch (IOException ex1) {
//                Logger.getLogger(EntityCore.class.getName()).log(Level.SEVERE, null, ex1);
//            }
        }
        return null;
    }
    
    /**
     * Allows for setting the root node directly in case it exists already.
     * 
     * The node will only be set if {@link JsonNode#isMissingNode()} does not
     * return true.
     * 
     * @param node - The {@link JsonNode}
     */
//    protected void setNode(JsonNode node) {
//        if(node.isMissingNode()) {
//            return;
//        }
//        
//        this.root = node;
//    }
}
