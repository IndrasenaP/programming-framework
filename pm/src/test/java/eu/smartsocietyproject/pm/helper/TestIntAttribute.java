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
public class TestIntAttribute extends Attribute {
    
    private int value;
    
    public TestIntAttribute() {
        
    }
    
    public TestIntAttribute(int value) {
        this.value = value;
    }

    @Override
    public Attribute clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public void parseValueFromString(String attributeValue) {
        this.value = Integer.parseInt(attributeValue);
    }
    
}
