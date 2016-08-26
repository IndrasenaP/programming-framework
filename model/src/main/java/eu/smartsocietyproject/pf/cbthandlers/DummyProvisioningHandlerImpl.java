package eu.smartsocietyproject.pf.cbthandlers;


import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.CollectiveBase;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.Optional;

public class DummyProvisioningHandlerImpl implements ProvisioningHandler{
    @Override
    public ApplicationBasedCollective provision(TaskRequest t, Optional<Collective> inputCollective) throws CBTLifecycleException {
        System.out.println("Doing some provisioning");
        try{Thread.sleep(200);}catch (InterruptedException ie){}
        return (ApplicationBasedCollective) CollectiveBase.emptyCollective();
    }
}
