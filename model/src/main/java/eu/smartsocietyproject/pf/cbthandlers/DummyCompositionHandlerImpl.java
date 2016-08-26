package eu.smartsocietyproject.pf.cbthandlers;


import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.List;

public class DummyCompositionHandlerImpl implements CompositionHandler{
    @Override
    public List<CollectiveWithPlan> compose(ApplicationBasedCollective provisioned, TaskRequest t) throws CBTLifecycleException {
        System.out.println("Doing some composition");
        try{Thread.sleep(4000);}catch (InterruptedException ie){}
        return null;
    }
}
