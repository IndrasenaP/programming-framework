package eu.smartsocietyproject.pf.orchestration.orchestratorManagerAPI;

import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.pf.Collective;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by zhenyuwen on 27/04/2016.
 */
public class ResourceOfOM extends Collective {
    public ResourceOfOM(Collective provisioned){
        super(provisioned, Optional.ofNullable(null));
    }

    public Collection<Peer> getMembers(){
        return super.getMembers();
    }
}
