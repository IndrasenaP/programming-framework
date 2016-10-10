package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * An attribute represents the value to a given name. Due to the different types
 * an attribute can have it is imperative that the attribute can parse itself
 * from {@link toJson()}.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public interface Attribute {

    public AttributeType getType();

    public abstract JsonNode toJson();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
