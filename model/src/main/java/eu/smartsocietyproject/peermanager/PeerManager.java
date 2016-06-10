package eu.smartsocietyproject.peermanager;

import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;
import eu.smartsocietyproject.peermanager.query.CollectiveQuery;
import eu.smartsocietyproject.pf.CollectiveBase;
import java.util.List;

public interface PeerManager {

    void persistCollective(CollectiveBase collective);
 
    /**
     * This function will load existing collectives which fit the given collective 
     * query.
     * @param query
     * @return 
     */
    List<CollectiveIntermediary> readCollectiveByQuery(CollectiveQuery query);

    /**
     * This function will load existing peers which fit the given peer query and
     * create a new collective they are in.
     * @param query
     * @return 
     */
    CollectiveIntermediary readCollectiveByQuery(PeerQuery query);

    CollectiveIntermediary readCollectiveById(String id);
}
