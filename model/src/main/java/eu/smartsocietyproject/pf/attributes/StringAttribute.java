/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.attributes;

import eu.smartsocietyproject.pf.Attribute;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class StringAttribute extends BasicValueAttribute<String> implements Attribute {

    public StringAttribute(String value) {
        super(value);
    }
    
    public static StringAttribute create(String value) {
        return new StringAttribute(value);
    }
    
    public static StringAttribute createFromJson(String json) {
        String value = parseValue(json, String.class);
        if(value != null) {
            return create(value);
        }
        return null;
    }
}
