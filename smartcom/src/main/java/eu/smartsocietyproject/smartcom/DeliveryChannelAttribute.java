/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.smartsocietyproject.pf.Attribute;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class DeliveryChannelAttribute implements Attribute {
    private static ObjectMapper mapper = new ObjectMapper();
    private String channelType;
    private String[] contactParams;

    public String getChannelType() {
        return channelType;
    }

    public String[] getContactParams() {
        return contactParams;
    }

    @Override
    public String toJson() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(DeliveryChannelAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public static DeliveryChannelAttribute create(JsonNode node) {
        return mapper.convertValue(node, DeliveryChannelAttribute.class);
    }
}
