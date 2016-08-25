/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.attributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.smartsocietyproject.pf.Attribute;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public abstract class BasicValueAttribute<T> implements Attribute {
    protected ObjectMapper mapper = new ObjectMapper();
    protected T value;
    private Class<T> clazz;
    
    protected BasicValueAttribute(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
    
    @Override
    public void parseJson(String attributeValue) {
        try {
            value = mapper.readValue(attributeValue, clazz);
        } catch (IOException ex) {
            Logger.getLogger(StringAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toJson() {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(StringAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof BasicValueAttribute)) {
            return false;
        }
        
        BasicValueAttribute that = (BasicValueAttribute)obj;
        
        if(!this.clazz.equals(that.clazz)) {
            return false;
        }
        
        return this.value.equals(that.value);
    }
    
    
}
