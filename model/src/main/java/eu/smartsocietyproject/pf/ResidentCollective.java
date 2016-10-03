package eu.smartsocietyproject.pf;

import com.google.common.collect.ImmutableMap;
import eu.smartsocietyproject.peermanager.CollectiveIntermediary;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;

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
        return ApplicationBasedCollective
                .of(this.getContext(), this.getId(), this.getKindInstance(), this.getMembers(), getAttributes());
    }

    @Override
    protected Collective.WithVisibleMembers makeMembersVisible() {
        return this;
    }


    static ResidentCollective createFromIntermediary(
        ApplicationContext context,
        Optional<String> kind,
        CollectiveIntermediary intermediary) throws PeerManagerException {
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
