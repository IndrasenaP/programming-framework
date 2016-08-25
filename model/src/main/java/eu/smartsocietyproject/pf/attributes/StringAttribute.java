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

    public StringAttribute() {
        super(String.class);
    }
    
    public static StringAttribute create() {
        return new StringAttribute();
    }
    
    public static StringAttribute create(String value) {
        StringAttribute att = new StringAttribute();
        att.setValue(value);
        return att;
    }
}
