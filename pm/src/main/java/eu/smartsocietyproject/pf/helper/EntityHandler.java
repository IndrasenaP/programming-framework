/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.BasicAttribute;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    protected EntityHandler(String json) {
        super(json);
    }

    protected EntityHandler(JsonNode node) {
        super(node);
    }

    /**
     * This function will either return the requested attribute as a
     * {@link BasicAttribute} with the expected {@link AttributeType} or throw
     * an exception if not possible.
     *
     * @param name
     * @param type
     * @return
     * @throws NullPointerException
     */
    public BasicAttribute getAttribute(String name, AttributeType type) throws PeerManagerException {
        Optional<Attribute> att = this.getAttribute(name);

        if (att.isPresent()
                && att.get() instanceof BasicAttribute
                && att.get().getType().equals(type)) {
            return (BasicAttribute) att.get();
        }

        //todo-sv: find better exception type
        throw new PeerManagerException(
                String.format("Attribute %s was not present or of wrong type!", name));
    }

    /**
     * This function will try to fetch the requested attribute. If it is a known
     * {@link BasicAttribute} it will be returned with its proper
     * {@link AttributeType} otherwise a {@link BasicAttribute} with the
     * {@link AttributeType#STRING} will be returned. The value in this case
     * represents the JSON-String.
     *
     * @param name
     * @return
     */
    public Optional<Attribute> getAttribute(String name) {
        JsonNode attNode = root.get(name);

        //map simple attributes
        for (AttributeType type : AttributeType.values()) {
            if (type.isValid(attNode)) {
                return type.fromJson(attNode);
            }
        }

        //map complex attributes
        String value = null;
        try {
            value = this.mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(attNode);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(EntityHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Optional.of(AttributeType.from(value));
    }

    public Map<String, Attribute> getAttributes() {
        return this.getAttributes(new ArrayList<>());
    }

    public Map<String, Attribute> getAttributes(List<String> specialAttributes) {
        return ImmutableList.copyOf(this.root.fieldNames())
                .stream()
                .filter(fieldName -> !specialAttributes.contains(fieldName))
                .map(fieldName -> EntityHandler.this.getAttribute(fieldName)
                        .map(v -> createEntry(fieldName, v)))
                .filter(optEntry -> optEntry.isPresent())
                .collect(Collectors
                        .toMap(optEntry -> optEntry.get().getKey(),
                                optEntry -> optEntry.get().getValue()));
    }

    private <V> Map.Entry<String, V> createEntry(final String s, final V a) {
        return new Map.Entry<String, V>() {
            @Override
            public String getKey() {
                return s;
            }

            @Override
            public V getValue() {
                return a;
            }

            @Override
            public V setValue(V value) {
                throw new UnsupportedOperationException("TODO"); // -=TODO=- (tommaso, 26/08/16)
            }
        };

    }

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

        private ObjectNode node;

        public Builder() {
            node = JsonNodeFactory.instance.objectNode();
        }

        public void addAttribute(String name, Attribute attribute) {
            node.set(name, attribute.toJson());
        }

        public void addAttributeNode(String name, EntityCore attribute) {
            node.set(name, attribute.root);
        }

        public EntityHandler build() {
            return new EntityHandler(node);
        }
    }

    //todo-sv: remove
    //WARNING: OLD CODE WILL SOON BE REMOVED
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
}
