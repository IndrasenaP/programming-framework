/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the base for every object which is represented by 
 * an arbitrary JSON.
 * It encapsulates the Root-{@link JsonNode} and the {@link ObjectMapper}.
 * Deriving classes give the right meaning to the root node by knowing how
 * to handle the arbitrary JSON.
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
    
    //todo-sv: check this
    /**
     * Will convert the object stored in the underlying JSON structure 
     * into a properly formated JSON-String.
     * 
     * @return - object as JSON-String
     */
    public JsonNode toJson(){
        return root;
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
        }
        return null;
    }
}
