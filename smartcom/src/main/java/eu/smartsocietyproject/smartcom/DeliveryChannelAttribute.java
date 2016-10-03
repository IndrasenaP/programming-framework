/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.smartsocietyproject.pf.helper.EntityCore;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class DeliveryChannelAttribute extends EntityCore {
    private static ObjectMapper mapper = new ObjectMapper();
    private String channelType;
    private String[] contactParams;

    protected DeliveryChannelAttribute(JsonNode json) {
        super(json);
    }
    
    public String getChannelType() {
        return channelType;
    }

    public String[] getContactParams() {
        return contactParams;
    }
    
    public static DeliveryChannelAttribute create(JsonNode node) {
        return new DeliveryChannelAttribute(node);
    }
}
