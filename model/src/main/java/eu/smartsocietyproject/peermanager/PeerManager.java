package eu.smartsocietyproject.peermanager;

import eu.smartsocietyproject.pf.Collective;

public interface PeerManager {

    void persistCollective(Collective collective);

    ResidentCollectiveIntermediary readCollectiveByQuery(PeerQuery query);

    ResidentCollectiveIntermediary readCollectiveById(String id);
}
