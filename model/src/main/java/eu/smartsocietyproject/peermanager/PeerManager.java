package eu.smartsocietyproject.peermanager;

import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.CollectiveQuery;
import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.Peer;
import eu.smartsocietyproject.pf.ResidentCollective;

import java.util.List;

public interface PeerManager {

    /**
     * This will persist the collective with the peer manager.
     * @param collective 
     */
    void persistCollective(ApplicationBasedCollective collective) throws PeerManagerException;
 
    /**
     * This function will load existing collectives which fit the given collective 
     * query.
     * @param query
     * @return 
     */
    List<ResidentCollective> findCollectives(CollectiveQuery query) throws PeerManagerException;

    /**
     * This function will load existing peers which fit the given peer query and
     * return a new collective holding this peers.
     * This new collective is not persisted.
     * @param query
     * @return 
     */
    ApplicationBasedCollective createCollectiveFromQuery(PeerQuery query, String kind) throws PeerManagerException;

    ApplicationBasedCollective createCollectiveFromQuery(PeerQuery query) throws PeerManagerException;
    
    /**
     * Reads a already persisted collective via the given id.
     * @param id
     * @return
     * @throws PeerManagerException 
     */
    ResidentCollective readCollectiveById(String id) throws PeerManagerException;

    Peer retrievePeer(String peerId) throws PeerManagerException;

    interface Factory {
        PeerManager create(ApplicationContext context);
    }
}
