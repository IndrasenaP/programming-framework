package eu.smartsocietyproject.pf;

import com.google.common.collect.ImmutableSet;
import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.PeerQuery;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class ResidentCollective extends Collective {
    private ResidentCollective(
        SmartSocietyApplicationContext context,
        String id,
        CollectiveKind kind,
        Collection<Peer> members,
        Map<String, Attribute> attributes) {
        super(context, id, kind, members, attributes);
    }

    public ApplicationBasedCollective toApplicationBasedCollective() {
        try {
            return
                ApplicationBasedCollective
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
    public ImmutableSet<Peer> getMembers() {
        return super.getMembers();
    }

    public static ResidentCollective createFromQuery(
        SmartSocietyApplicationContext context,
        PeerQuery query) throws PeerManagerException {
        PeerManager peerManager = context.getPeerManager();
        throw new UnsupportedOperationException("Still to be implemented");
    }

    public static ResidentCollective createFromId(
        SmartSocietyApplicationContext context,
        String id,
        Optional<String> verify_kind) throws PeerManagerException {
        PeerManager peerManager = context.getPeerManager();

        throw new UnsupportedOperationException("Still to be implemented");
    }

}
