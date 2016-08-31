/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import at.ac.tuwien.dsg.smartcom.callback.CollectiveInfoCallback;
import at.ac.tuwien.dsg.smartcom.callback.PeerAuthenticationCallback;
import at.ac.tuwien.dsg.smartcom.callback.PeerInfoCallback;
import at.ac.tuwien.dsg.smartcom.callback.exception.NoSuchCollectiveException;
import at.ac.tuwien.dsg.smartcom.callback.exception.NoSuchPeerException;
import at.ac.tuwien.dsg.smartcom.callback.exception.PeerAuthenticationException;
import at.ac.tuwien.dsg.smartcom.model.CollectiveInfo;
import at.ac.tuwien.dsg.smartcom.model.DeliveryPolicy;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.IdentifierType;
import at.ac.tuwien.dsg.smartcom.model.PeerInfo;
import at.ac.tuwien.dsg.smartcom.model.PrivacyPolicy;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class SmartComPeerManager implements PeerAuthenticationCallback, PeerInfoCallback, CollectiveInfoCallback {
    
    private PeerManager peerManager;
    
    @Override
    public boolean authenticate(Identifier peerId, String password) throws PeerAuthenticationException {
        //todo-sv: figure out how to integrate properly with local and remote PM
        return true;
    }

    @Override
    public PeerInfo getPeerInfo(Identifier id) throws NoSuchPeerException {
        //PeerInfo info = new PeerInfo
        DeliveryAddressesAttribute addresses = DeliveryAddressesAttribute
                .createFromPeerIntermediary(this.peerManager
                        .readPeerById(id.getId()));
        
        return new PeerInfo(id, 
                DeliveryPolicy.Peer.AT_LEAST_ONE, 
                ImmutableList.<PrivacyPolicy>of(), 
                addresses.getChannels());
    }

    @Override
    public CollectiveInfo getCollectiveInfo(Identifier collective) throws NoSuchCollectiveException {
        CollectiveIntermediary coll = this.peerManager
                .readCollectiveById(collective.getId());
        
        List<Identifier> peerIdentifiers = new ArrayList<>();
        coll.getMembers().forEach(peer -> peerIdentifiers
                .add(new Identifier(IdentifierType.PEER, peer.getId(), null)));
        
        //todo-sv: which delivery policy to set?
        return new CollectiveInfo(
                collective,
                peerIdentifiers, 
                DeliveryPolicy.Collective.TO_ANY);
    }
    
}
