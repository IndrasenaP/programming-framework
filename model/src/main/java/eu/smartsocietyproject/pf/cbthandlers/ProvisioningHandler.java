package eu.smartsocietyproject.pf.cbthandlers;

import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.Optional;

public interface ProvisioningHandler{
    ApplicationBasedCollective provision(TaskRequest t, Optional<Collective> inputCollective) throws CBTLifecycleException; //returns provisioned
}
