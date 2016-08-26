package eu.smartsocietyproject.pf.cbthandlers;

import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.CollectiveBase;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.Plan;

import java.util.List;

public class DummyNegotiationHandlerImpl implements NegotiationHandler{
    @Override
    public CollectiveWithPlan negotiate(List<CollectiveWithPlan> negotiables) throws CBTLifecycleException {
        System.out.println("Doing some negotiation");
        try{Thread.sleep(200);}catch (InterruptedException ie){}
        System.out.println("Finished doing negotiation");
        return CollectiveWithPlan.of((ApplicationBasedCollective) CollectiveBase.emptyCollective(), new Plan());
    }
}
