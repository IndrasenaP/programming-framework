package eu.smartsocietyproject.pf.cbthandlers;

import akka.actor.Actor;
import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.Optional;

public interface ProvisioningHandler {
    ApplicationBasedCollective provision(
        ApplicationContext context,
        TaskRequest t,
        Optional<Collective> inputCollective)
        throws CBTLifecycleException; //returns provisioned
}