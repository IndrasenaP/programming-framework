/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.BasicAttribute;
import java.io.IOException;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerChannelAddressAdapter {    
    public static PeerChannelAddress convert(BasicAttribute<String> attribute) {
        try {
            return ObjectMapperSingelton.getObjectMapper()
                    .readValue(attribute.getValue(), PeerChannelAddress.class);
        } catch (IOException ex) {
            //todo-sv: remove latter
            ex.printStackTrace();
            throw new IllegalStateException(ex);
        }
    }
    
    public static Attribute convert(PeerChannelAddress address) {
        try {
            Attribute att = AttributeType.from(ObjectMapperSingelton.getObjectMapper()
                    .writerWithDefaultPrettyPrinter().writeValueAsString(address));
            return att;
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
