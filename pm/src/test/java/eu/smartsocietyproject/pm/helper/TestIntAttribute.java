/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pm.helper;

import eu.smartsocietyproject.pf.Attribute;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class TestIntAttribute implements Attribute {
    
    private int value;
    
    public TestIntAttribute() {
        
    }
    
    public TestIntAttribute(int value) {
        this.value = value;
    }

    @Override
    public String toJson() {
        return String.valueOf(this.value);
    }

    @Override
    public void parseJson(String attributeValue) {
        this.value = Integer.parseInt(attributeValue);
    }
    
}
