package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.Optional;

public enum AttributeType {
    INTEGER {
        @Override
        public boolean isValid(JsonNode node) {
            return node.isInt();
        }
    },
    DOUBLE {
        @Override
        public boolean isValid(JsonNode node) {
            return node.isDouble();
        }
    },
    STRING {
        @Override
        public boolean isValid(JsonNode node) {
            return node.isTextual();
        }
    };

    private static final ObjectMapper mapper=new ObjectMapper();

    public Optional<Attribute> fromJson(final JsonNode node) {
        if ( isValid(node) ) {
            return Optional.empty();
        }
        return Optional.of(new Attribute(this) {
            @Override
            public JsonNode toJson() {
                return node.deepCopy();
            }
        });
    }

    public abstract boolean isValid(JsonNode node);

    public boolean isOfValidType(Attribute attribute) {
        return attribute.getType().equals(this);
    }

    public static Attribute from(final int value) {
        return new Attribute(INTEGER) {
            @Override
            public JsonNode toJson() {
                return IntNode.valueOf(value);
            }
        };
    }

    public static Attribute from(final double value) {
        return new Attribute(DOUBLE) {
            @Override
            public JsonNode toJson() {
                return DoubleNode.valueOf(value);
            }
        };
    }

    public static Attribute from(final String value) {
        return new Attribute(STRING) {
            @Override
            public JsonNode toJson() {
                return TextNode.valueOf(value);
            }
        };
    }

}
