/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.BasicAttribute;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerChannelAddressesAdapter {
    public static List<PeerChannelAddress> convert(BasicAttribute<String> att) {
        try {
            return ObjectMapperSingelton.getObjectMapper()
                    .readValue(att.getValue(),
                            new TypeReference<List<PeerChannelAddress>>() {});
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    public static Attribute convert(List<PeerChannelAddress> addresses) {
        try {
            return AttributeType.from(ObjectMapperSingelton.getObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(addresses));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
