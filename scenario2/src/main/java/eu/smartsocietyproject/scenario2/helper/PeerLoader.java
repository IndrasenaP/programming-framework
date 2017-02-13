/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario2.helper;

import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.SmartSocietyApplicationContext;
import eu.smartsocietyproject.pf.helper.InternalPeerManager;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;
import eu.smartsocietyproject.smartcom.PeerChannelAddressAdapter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerLoader {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static void laodPeers(SmartSocietyApplicationContext context) throws IOException {
        InternalPeerManager pm = (InternalPeerManager) context.getPeerManager();
        
        List<JsonPeer> peers = mapper.readValue(Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("Peers.json"),
                mapper.getTypeFactory()
                        .constructCollectionType(List.class, JsonPeer.class));

        peers.stream().forEach(peer -> {
            pm.persistPeer(PeerIntermediary
                    .builder(peer.getName(), peer.getRole())
                    .addDeliveryAddress(PeerChannelAddressAdapter
                            .convert(new PeerChannelAddress(
                                    Identifier.peer(peer.getName()),
                                    Identifier.channelType(peer.getChannelType()),
                                    Arrays.asList(peer.getChannel()))
                            )
                    )
                    .addAttribute("restaurantQA", AttributeType.from("true"))
                    .build());
        });
    }
}
