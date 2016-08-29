package eu.smartsocietyproject.pf;

import com.google.common.collect.ImmutableMap;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class ResidentCollective extends Collective.WithVisibleMembers {

    private ResidentCollective(
            ApplicationContext context,
            String id,
            CollectiveKind kind,
            Collection<Member> members,
            Map<String, Attribute> attributes) {
        super(context, id, kind, members, attributes);
    }

    public ApplicationBasedCollective toApplicationBasedCollective() {
        try {
            return ApplicationBasedCollective
                    .of(this.getContext(), this.getId(), this.getKindInstance(), this.getMembers())
                    .withAttributes(getAttributes());
        } catch (CollectiveCreationException e) {
            throw new IllegalStateException(
                    String.format(
                            "Failed creation of an Application Based Collective from a Resident Collective: %s",
                            toString()), e);
        }
    }

    @Override
    protected Collective.WithVisibleMembers makeMembersVisible() {
        return this;
    }


    static ResidentCollective createFromIntermediary(
        ApplicationContext context,
        Optional<String> kind,
        CollectiveIntermediary intermediary) {
            CollectiveKind collectiveKind =
                kind.flatMap( k -> context.getKindRegistry().get(k))
                .orElse(CollectiveKind.EMPTY);

        Map<String, Attribute> attributes = intermediary.getAttributes(collectiveKind);

        return new ResidentCollective(context,
                                      intermediary.getId(),
                                      collectiveKind,
                                      intermediary.getMembers(),
                                      attributes);
    }

    public static ResidentCollective createFromId(
            ApplicationContext context,
            String id,
            Optional<String> verify_kind) throws PeerManagerException {
        PeerManager peerManager = context.getPeerManager();
        return peerManager.readCollectiveById(id);
    }

}
