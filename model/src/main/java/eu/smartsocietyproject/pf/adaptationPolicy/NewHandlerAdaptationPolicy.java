package eu.smartsocietyproject.pf.adaptationPolicy;

import akka.actor.ActorRef;
import eu.smartsocietyproject.pf.enummerations.State;

public class NewHandlerAdaptationPolicy implements CompositionAdaptationPolicy, ExecutionAdaptationPolicy,
                                            NegotiationAdaptationPolicy, ProvisioningAdaptationPolicy, QualityAssuranceAdaptionPolicy{

    private State state;

    public NewHandlerAdaptationPolicy(State state) {
        this.state = state;
    }

    @Override
    public State adapt(ActorRef actorRef) {
        switch (state){
            case PROV_FAIL:
                return State.PROVISIONING;
            case COMP_FAIL:
                return State.COMPOSITION;
            case NEG_FAIL:
                return State.NEGOTIATION;
            case EXEC_FAIL:
                return State.EXECUTION;
            case QUALITY_ASSURANCE_FAIL:
                return State.QUALITY_ASSURANCE;
            case ORCH_FAIL:
                return State.CONTINUOUS_ORCHESTRATION;
            default:
                return this.state;
        }
    }
}
