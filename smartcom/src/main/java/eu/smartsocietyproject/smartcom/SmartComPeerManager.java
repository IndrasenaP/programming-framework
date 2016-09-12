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
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.ResidentCollective;

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerInfo getPeerInfo(Identifier id) throws NoSuchPeerException {
        //PeerInfo info = new PeerInfo
        //this.peerManager.
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CollectiveInfo getCollectiveInfo(Identifier collective) throws NoSuchCollectiveException {
        ResidentCollective coll;
        try {
            coll = this.peerManager.readCollectiveById(collective.getId());
        } catch (PeerManagerException e) {
            throw new  NoSuchCollectiveException();
        }

        List<Identifier> peerIdentifiers = new ArrayList<>();
        coll.getMembers().forEach(member -> peerIdentifiers
                .add(new Identifier(IdentifierType.PEER, member.getPeerId(), null)));
        
        //todo-sv: which delivery policy to set?
        return new CollectiveInfo(
                new Identifier(IdentifierType.COLLECTIVE, 
                    coll.getId(), 
                    null), 
                peerIdentifiers, 
                DeliveryPolicy.Collective.TO_ANY);
    }
    
}
