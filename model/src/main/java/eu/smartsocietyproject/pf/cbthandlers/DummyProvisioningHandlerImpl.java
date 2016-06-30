package eu.smartsocietyproject.pf.cbthandlers;


import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.CollectiveBase;
import eu.smartsocietyproject.pf.TaskRequest;

public class DummyProvisioningHandlerImpl implements ProvisioningHandler{
    @Override
    public Collective provision(TaskRequest t, Collective inputCollective) throws CBTLifecycleException {
        System.out.println("Doing some provisioning");
        try{Thread.sleep(200);}catch (InterruptedException ie){}
        return CollectiveBase.emptyCollective();
    }
}
