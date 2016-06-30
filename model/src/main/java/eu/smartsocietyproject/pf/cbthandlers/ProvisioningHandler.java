package eu.smartsocietyproject.pf.cbthandlers;

import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.TaskRequest;

public interface ProvisioningHandler{
    Collective provision(TaskRequest t, Collective inputCollective) throws CBTLifecycleException; //returns provisioned
}
