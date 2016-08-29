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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the base for classes that represent objects which have
 * arbitrary attributes.
 *
 * It can be used by extension or as standalone instance and provides general
 * functions for retrieving attributes.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class EntityHandler extends EntityCore {

//    protected final String keyId;
    protected EntityHandler(String json) {
        super(json);
    }

    protected EntityHandler(JsonNode node) {
        super(node);
    }

//    protected void setId(String id) {
//        this.addAttributeValue(this.keyId, StringAttribute.create(id));
//    }
    //todo-sv: is direct access to the id in this general class really a good idea?
    /**
     * Returns the id of the represented object if existing.
     *
     * @return - id or empty string
     */
//    public String getId() {
//        if(this.keyId.isEmpty()) {
//            return "";
//        }
//        
//        return this.getAttributeValue(keyId, StringAttribute.create()).getValue();
//    }
    /**
     * Allows unrestricted access to the attributes in the JSON structure.
     *
     * @param <T> - type of the actual attribute passed in
     * @param name - the name of the attribute to retrieve
     * @param attribute - the attribute object into which to parse the attribute
     * @return - returns the passed in attribute with data if any was found
     */
//    protected <T extends Attribute> T getAttributeValue(String name, T attribute) {
//        try {
//            attribute.parseJson(mapper
//                    .writerWithDefaultPrettyPrinter()
//                    .writeValueAsString(root.path(name)));
//        } catch (JsonProcessingException ex) {
//            Logger.getLogger(EntityCore.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return attribute;
//    }
    /**
     * Allows for directly setting the {@link JsonNode} into attributes that
     * work with the same approach.
     *
     * @param <T> - type of the actual attribute passed in
     * @param name - the name of the attribute to retrieve
     * @param attribute - the attribute object into which to parse the attribute
     * @return - returns the passed in attribute with data if any was found
     */
//    protected <T extends EntityCore> T getAttributeNode(String name, T attribute) {
//        attribute.setNode(root.path(name));
//        return attribute;
//    }
    /**
     * Will return the requested attribute if it exists. You can not request the
     * ID this way. The attribute object will be populated by using the
     * {@link Attribute#parseJson(java.lang.String)} function.
     *
     * @param <T> - type of the actual attribute passed in
     * @param name - the name of the attribute to retrieve
     * @param attribute - the attribute object into which to parse the attribute
     * @return - returns the passed in attribute with data if any was found
     */
//    public <T extends Attribute> T getAttribute(String name, T attribute) {
//        if(this.keyId.equals(name)) {
//            return attribute;
//        }
//        
//        //todo-sv: why not allow access to the id here? it can not be manipulated
//        //this way due to the string conversion?
//        
//        return this.getAttributeValue(name, attribute);
//    }
    public String getAttribute(String name) {
        return root.get(name).asText();
    }

    /**
     * This will allow directly adding the {@link JsonNode} of an attribute to
     * this object.
     *
     * This will replace the old value if any existed.
     *
     * @param name - the name of the attribute to add
     * @param attribute - the attribute object
     */
    /**
     * This will allow setting any attribute value.
     *
     * The attribute value will retrieved through the {@link Attribute#toJson()}
     * function.
     *
     * This will replace the old value if any existed.
     *
     * @param name - the name of the attribute to add
     * @param attribute - the attribute object
     */
//    protected void addAttributeValue(String name, Attribute attribute) {
//        //todo-sv:add exception for error handling
//        if(this.root.isObject()) {
//            try {
//                JsonNode node = mapper.readTree(attribute.toJson());
//                ((ObjectNode)root).set(name, node);
//            } catch (IOException ex) {
//                Logger.getLogger(EntityCore.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
    /**
     * This function allows you setting a certain attribute to this entity.
     *
     * Manipulating the ID of the entity is not possible by this function.
     *
     * This will replace the old value if any existed.
     *
     * @param name - the name of the attribute to add
     * @param attribute - the attribute object
     */
//    public void addAttribute(String name, Attribute attribute) {
//        if(this.keyId.equals(name)) {
//           return; 
//        }
//        
//        addAttributeValue(name, attribute);
//    }
    public static EntityHandler create(String json) {
        return new EntityHandler(json);
    }

    public static EntityHandler create(JsonNode node) {
        return new EntityHandler(node);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private EntityHandler handler;

        public Builder() {
            handler = EntityHandler.create("{}");
        }
        
        public void addAttribute(String name, Attribute attribute) {
            if(handler.root.isObject()) {
                ((ObjectNode) handler.root).set(name,
                        attribute.toJson());
            }
        }

        public void addAttributeNode(String name, EntityCore attribute) {
            if (handler.root.isObject()) {
                ((ObjectNode) handler.root).set(name, attribute.root);
            }
        }
        
        public EntityHandler build() {
            EntityHandler done = handler;
            handler = EntityHandler.create("{}");
            return done;
        }
    }
}
