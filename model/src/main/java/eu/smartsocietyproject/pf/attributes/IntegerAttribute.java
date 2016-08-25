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
public class IntegerAttribute extends BasicValueAttribute<Integer> implements Attribute {
    
    public IntegerAttribute(int value) {
        super(value);
    }    
    
    public static IntegerAttribute create(int value) {
        return new IntegerAttribute(value);
    }
    
    public static IntegerAttribute createFromJson(String json) {
        Integer value = parseValue(json, Integer.class);
        if(value != null) {
            return create(value);
        }
        return null;
    }
}
