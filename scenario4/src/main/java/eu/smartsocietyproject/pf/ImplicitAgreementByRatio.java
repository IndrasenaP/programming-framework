package eu.smartsocietyproject.pf;

import com.google.common.base.Preconditions;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.NegotiationHandler;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ImplicitAgreementByRatio implements NegotiationHandler {
    private final Optional<Double> ratio;

    public ImplicitAgreementByRatio(double ratio) {
        Preconditions.checkArgument(ratio <= 1.0 && ratio > 0, "Error negotiation ratio should be in the range (0,1]");
        this.ratio = Optional.of(ratio);
    }

    public ImplicitAgreementByRatio() {
        this.ratio = Optional.empty();
    }


    @Override
    public CollectiveWithPlan negotiate(ApplicationContext context, List<CollectiveWithPlan> negotiables)
        throws CBTLifecycleException {

        if (negotiables.size() < 1) {
            throw new CBTLifecycleException("At least one plan required");
        }

        if (!ratio.isPresent()) {
            return negotiables.get(0);
        }
        Collective inputCollective = negotiables.get(0).getCollective();
        String kind = inputCollective.getKind();
        Set<Member> members = inputCollective.makeMembersVisible().getMembers();
        int returnedMembers = (int)(members.size()*ratio.get());
        List<Member> selectedMembers = members.stream().limit(returnedMembers).collect(Collectors.toList());

        try {
            return
                CollectiveWithPlan.of(
                    ApplicationBasedCollective.empty(context, UUID.randomUUID().toString(), kind)
                                              .withMembers(selectedMembers),
                    negotiables.get(0).getPlan());
        } catch (Collective.CollectiveCreationException e) {
            throw new CBTLifecycleException("Unable to create collection", e);
        }
    }
}
