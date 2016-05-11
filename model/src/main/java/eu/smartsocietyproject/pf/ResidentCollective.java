package eu.smartsocietyproject.pf;

import com.google.common.collect.ImmutableSet;
import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.PeerQuery;
import eu.smartsocietyproject.peermanager.ResidentCollectiveIntermediary;

import java.util.Collection;
import java.util.HashMap;
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
    public ImmutableSet<Peer> getMembers() {
        return super.getMembers();
    }

    public static ResidentCollective createFromQuery(
            SmartSocietyApplicationContext context,
            PeerQuery query) throws PeerManagerException {
        PeerManager peerManager = context.getPeerManager();
        ResidentCollectiveIntermediary collective
                = peerManager.readCollectiveByQuery(query);
        //todo-sv: createFromQuery missing kind?
        return new ResidentCollective(context,
                collective.getId(),
                null, //todo-sv figure this out 
                collective.getMembers(),
                new HashMap<>());
    }

    public static ResidentCollective createFromId(
            SmartSocietyApplicationContext context,
            String id,
            Optional<String> verify_kind) throws PeerManagerException {
        PeerManager peerManager = context.getPeerManager();
        ResidentCollectiveIntermediary collective
                = peerManager.readCollectiveById(id);
        //todo-sv: verify kind with context
        return new ResidentCollective(context,
                id,
                null, //todo-sv figure this out 
                collective.getMembers(),
                new HashMap<>()); //todo-sv should at some point also be provided by PM
    }

}
