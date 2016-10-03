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
     * @throws PeerManagerException
     */
    @SuppressWarnings("unchecked")
    public <T> BasicAttribute<T> getAttribute(String name, AttributeType type) throws PeerManagerException {
        Optional<Attribute> att = this.getAttribute(name);

        if (att.isPresent()
                && att.get() instanceof BasicAttribute
                && att.get().getType().equals(type)) {
            return (BasicAttribute<T>) att.get();
        }

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
}
