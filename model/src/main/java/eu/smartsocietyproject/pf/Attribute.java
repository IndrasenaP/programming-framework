package eu.smartsocietyproject.pf;

/**
 * An attribute represents the value to a given name. Due to the different types
 * an attribute can have it is imperative that the attribute can parse itself
 * from {@link toJson()}.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public interface Attribute {    
    public void parseJson(String attributeValue);
    
    public String toJson();
}
