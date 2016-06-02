package eu.smartsocietyproject.peermanager;

import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;
import eu.smartsocietyproject.pf.CollectiveBase;

public interface PeerManager {

    void persistCollective(CollectiveBase collective);

    CollectiveIntermediary readCollectiveByQuery(PeerQuery query);

    CollectiveIntermediary readCollectiveById(String id);
}
