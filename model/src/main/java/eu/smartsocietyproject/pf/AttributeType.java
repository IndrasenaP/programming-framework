package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Optional;

public enum AttributeType {
    INTEGER {
        @Override
        public boolean isValid(JsonNode node) {
            return node.isInt();
        }
        @Override
        public Attribute getAttributeFromValidJson(final JsonNode node) {
            return new BasicAttribute<Integer>(this, node.intValue()) {
                @Override
                public JsonNode toJson() {
                    return node.deepCopy();
                }
            };
        }

    },
    DOUBLE {
        @Override
        public boolean isValid(JsonNode node) {
            return node.isDouble();
        }
        @Override
        public Attribute getAttributeFromValidJson(final JsonNode node) {
            return new BasicAttribute<Double>(this, node.doubleValue()) {
                @Override
                public JsonNode toJson() {
                    return node.deepCopy();
                }
            };
        }
    },
    STRING {
        @Override
        public boolean isValid(JsonNode node) {
            return node.isTextual();
        }
        @Override
        public Attribute getAttributeFromValidJson(final JsonNode node) {
            return new BasicAttribute<String>(this, node.textValue()) {
                @Override
                public JsonNode toJson() {
                    return node.deepCopy();
                }
            };
        }
    };

    private static final ObjectMapper mapper=new ObjectMapper();

    public Optional<Attribute> fromJson(final JsonNode node) {
        if ( isValid(node) ) {
            return Optional.empty();
        }
        return Optional.of(getAttributeFromValidJson(node));
    }

    protected abstract Attribute getAttributeFromValidJson(JsonNode node);

    public abstract boolean isValid(JsonNode node);

    public boolean isOfValidType(Attribute attribute) {
        return attribute.getType().equals(this);
    }

    public static Attribute from(final int value) {
        return new BasicAttribute<Integer>(INTEGER, value) {
            @Override
            public JsonNode toJson() {
                return IntNode.valueOf(value);
            }

        };
    }

    public static Attribute from(final double value) {
        return new BasicAttribute<Double>(DOUBLE, value) {
            @Override
            public JsonNode toJson() {
                return DoubleNode.valueOf(value);
            }

        };
    }

    public static Attribute from(final String value) {
        return new BasicAttribute<String>(STRING, value) {
            @Override
            public JsonNode toJson() {
                return TextNode.valueOf(value);
            }
        };
    }

    private abstract static class BasicAttribute<T> extends Attribute {
        private T value;
        protected BasicAttribute(AttributeType type, T value) {
            super(type);
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BasicAttribute that = (BasicAttribute) o;

            return
                Objects.equal(this.value, that.value) &&
                Objects.equal(this.getType(), that.getType());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getType(), value);
        }


        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                              .add("value", value)
                              .add("type", getType())
                              .toString();
        }
    }

}
