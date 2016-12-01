package eu.smartsocietyproject.pf;

import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ProvisioningHandler;

import java.util.Optional;

public class IdentityProvisioningHandler implements ProvisioningHandler {
    @Override
    public ApplicationBasedCollective provision(
        ApplicationContext context, TaskRequest t, Optional<Collective> inputCollective) throws CBTLifecycleException {
        return inputCollective.get().toApplicationBasedCollective();
    }
}
