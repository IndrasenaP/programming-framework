/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.helper;

import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.pf.Peer;
import java.util.List;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public abstract class InternalPeerManager implements PeerManager {
    public abstract void persistPeer(PeerIntermediary peer);
    public abstract PeerIntermediary readPeerById(String peerId) throws PeerManagerException;
    public abstract List<PeerIntermediary> findPeers(PeerQuery query) throws PeerManagerException;
    
    @Override
    public Peer retrievePeer(String peerId) throws PeerManagerException {
        PeerIntermediary intermediary = readPeerById(peerId);
        return new Peer(peerId, intermediary.toJson());
    }
}
