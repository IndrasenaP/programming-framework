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
    
    public IntegerAttribute() {
        super(Integer.class);
    }    
    
    public static IntegerAttribute create() {
        return new IntegerAttribute();
    }
    
    public static IntegerAttribute create(int value) {
        IntegerAttribute att = new IntegerAttribute();
        att.setValue(value);
        return att;
    }
}
