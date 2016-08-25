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
    private static ObjectMapper mapper = new ObjectMapper();
    private final T value;
    
    protected BasicValueAttribute(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
    
    protected static <T> T parseValue(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException ex) {
            Logger.getLogger(StringAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
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
        
        if(!this.getClass().equals(that.getClass())) {
            return false;
        }
        
        return this.value.equals(that.value);
    }
}
