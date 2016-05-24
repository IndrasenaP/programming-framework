package eu.smartsocietyproject.pf;

/**
 * An attribute represents the value to a given name. Due to the different types
 * an attribute can have it is imperative that the attribute can parse itself
 * from {@link toString()}.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public abstract class Attribute {

    public Attribute() {

    }

    public abstract Attribute clone();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

    public abstract void parseValueFromString(String attributeValue);
}
