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
import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import at.ac.tuwien.dsg.smartcom.model.PeerInfo;
import at.ac.tuwien.dsg.smartcom.model.PrivacyPolicy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.ResidentCollective;
import eu.smartsocietyproject.pf.helper.InternalPeerManager;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class SmartComPeerManager implements PeerAuthenticationCallback, PeerInfoCallback, CollectiveInfoCallback {

    private static final String deliveryAddress = "deliveryAddress";
    private static final String deliveryAddresses = "deliveryAddresses";
    private InternalPeerManager peerManager;

    //todo-sv: context should handle wireing
    public SmartComPeerManager(InternalPeerManager peerManager) {
        this.peerManager = peerManager;
    }

    @Override
    public boolean authenticate(Identifier peerId, String password) throws PeerAuthenticationException {
        //todo-sv: figure out how to integrate properly with local and remote PM
        return true;
    }

    @Override
    public PeerInfo getPeerInfo(Identifier id) throws NoSuchPeerException {
        //PeerInfo info = new PeerInfo
//        DeliveryAddressesAttribute addresses;
//        try {
//            addresses = DeliveryAddressesAttribute
//                    .createFromPeerIntermediary(this.peerManager
//                            .readPeerById(id.getId()));
//        } catch (PeerManagerException ex) {
//            throw new IllegalStateException(ex);
//        }

        PeerIntermediary peer;
        try {
            peer = this.peerManager.readPeerById(id.getId());
        } catch (PeerManagerException ex) {
            throw new IllegalStateException(ex);
        }
        List<PeerChannelAddress> addresses = new ArrayList<>();

        try {
            String fieldName = deliveryAddress;
            
            PeerChannelAddress add = PeerChannelAddressAdapter
                    .convert(peer.getAttribute(deliveryAddress,
                                AttributeType.STRING));
            add.setPeerId(id);
            addresses.add(add);
            
//            if(peer.hasAttribute(deliveryAddresses)) {
//                addresses = PeerChannelAddressesAdapter
//                        .convert(peer.getAttribute(deliveryAddress, 
//                                AttributeType.STRING));
//            }
        } catch (PeerManagerException ex) {
            throw new IllegalStateException(ex);
        }

        return new PeerInfo(id,
                DeliveryPolicy.Peer.AT_LEAST_ONE,
                ImmutableList.<PrivacyPolicy>of(),
                addresses);
    }

    @Override
    public CollectiveInfo getCollectiveInfo(Identifier collective) throws NoSuchCollectiveException {
        ResidentCollective coll;
        try {
            coll = this.peerManager.readCollectiveById(collective.getId());
        } catch (PeerManagerException e) {
            throw new NoSuchCollectiveException();
        }

        List<Identifier> peerIdentifiers = new ArrayList<>();
        coll.getMembers().forEach(member -> peerIdentifiers
                .add(new Identifier(IdentifierType.PEER, member.getPeerId(), null)));

        //todo-sv: which delivery policy to set?
        return new CollectiveInfo(
                collective,
                peerIdentifiers,
                DeliveryPolicy.Collective.TO_ANY);
    }

}
