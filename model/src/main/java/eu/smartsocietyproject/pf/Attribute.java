package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * An attribute represents the value to a given name. Due to the different types
 * an attribute can have it is imperative that the attribute can parse itself
 * from {@link toJson()}.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public abstract class Attribute {
    //public void parseJson(String attributeValue);
    private final AttributeType type;

    protected Attribute(AttributeType type) {
        this.type = type;
    }

    public AttributeType getType() {
        return type;
    }

    public abstract JsonNode toJson();
}
