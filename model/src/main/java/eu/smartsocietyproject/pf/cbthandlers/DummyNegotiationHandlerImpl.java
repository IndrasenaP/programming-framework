package eu.smartsocietyproject.pf.cbthandlers;

import eu.smartsocietyproject.pf.*;

import java.util.List;

public class DummyNegotiationHandlerImpl implements NegotiationHandler{
    @Override
    public CollectiveWithPlan negotiate(List<CollectiveWithPlan> negotiables) throws CBTLifecycleException {
        System.out.println("Doing some negotiation");
        try{Thread.sleep(200);}catch (InterruptedException ie){}
        System.out.println("Finished doing negotiation");
        try {
            return CollectiveWithPlan.of(ApplicationBasedCollective.empty(null, "", ""), new Plan());
        } catch (Collective.CollectiveCreationException e) {
            return null;
        }
    }
}
