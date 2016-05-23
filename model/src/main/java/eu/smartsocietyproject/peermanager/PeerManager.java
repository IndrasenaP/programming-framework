package eu.smartsocietyproject.peermanager;

import eu.smartsocietyproject.peermanager.helper.ResidentCollectiveIntermediary;
import eu.smartsocietyproject.pf.CollectiveBase;

public interface PeerManager {

    void persistCollective(CollectiveBase collective);

    ResidentCollectiveIntermediary readCollectiveByQuery(PeerQuery query);

    ResidentCollectiveIntermediary readCollectiveById(String id);
}
