/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.helper.EntityCore;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;
import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.BasicAttribute;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class DeliveryAddressesAttribute extends EntityCore {
    
    private final String peerId;
    
    protected DeliveryAddressesAttribute(String json, String peerId) {
        super(json);
        this.peerId = peerId;
    }
    
    public List<PeerChannelAddress> getChannels() {
        ImmutableList.Builder<PeerChannelAddress> builder = ImmutableList.builder();
        if(this.root.isArray()) {
            for(JsonNode node: root) {
                DeliveryChannelAttribute channel = DeliveryChannelAttribute.create(node);
                builder.add(new PeerChannelAddress(Identifier.peer(peerId),
                        Identifier.channelType(channel.getChannelType()), 
                        Arrays.asList(channel.getContactParams())));
            }
        }
        return builder.build();
    }
    
    protected static DeliveryAddressesAttribute createFromPeerIntermediary(PeerIntermediary peer) throws PeerManagerException {
        BasicAttribute<String> value = peer
                .getAttribute("deliveryAddress", AttributeType.STRING);
        return new DeliveryAddressesAttribute(value.getValue(), peer.getPeerId());
    }
}
