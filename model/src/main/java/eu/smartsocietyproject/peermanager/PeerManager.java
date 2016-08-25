package eu.smartsocietyproject.peermanager;

import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;
import eu.smartsocietyproject.peermanager.query.CollectiveQuery;
import java.util.List;

public interface PeerManager {

    /**
     * This will persist the collective with the peer manager.
     * @param collective 
     */
    void persistCollective(CollectiveIntermediary collective);
 
    /**
     * This function will load existing collectives which fit the given collective 
     * query.
     * @param query
     * @return 
     */
    List<CollectiveIntermediary> readCollectiveByQuery(CollectiveQuery query);

    /**
     * This function will load existing peers which fit the given peer query and
     * return a new collective holding this peers.
     * This new collective is not persisted.
     * @param query
     * @return 
     */
    CollectiveIntermediary readCollectiveByQuery(PeerQuery query);

    /**
     * Reads a already persisted collective via the given id.
     * @param id
     * @return 
     */
    CollectiveIntermediary readCollectiveById(String id);
}
