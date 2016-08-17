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
public class TestStringAttribute implements Attribute {
    
    private String value;
    
    public TestStringAttribute() {
        
    }
    
    public TestStringAttribute(String value) {
        this.value = value;
    }

    @Override
    public String toJson() {
        return this.value;
    }

    @Override
    public void parseJson(String attributeValue) {
        this.value = attributeValue;
    }
    
}
