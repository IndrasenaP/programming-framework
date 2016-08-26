package eu.smartsocietyproject.pf;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class ResidentCollective extends Collective.WithVisibleMembers {

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
    protected Collective.WithVisibleMembers makeMembersVisible() {
        return this;
    }


    public static ResidentCollective createFromQuery(
            DefaultSmartSocietyApplicationContext context,
            PeerQuery query) throws PeerManagerException {
        PeerManager peerManager = context.getPeerManager();
        CollectiveIntermediary collective
                = peerManager.readCollectiveByQuery(query);
        //todo-sv: createFromQuery missing kind?
        return new ResidentCollective(context,
                collective.getId(),
                null, //todo-sv figure this out 
                collective.getMembers(),null);
                //todo-sv: fix this collective.getAttributes());
    }

    public static ResidentCollective createFromId(
            SmartSocietyApplicationContext context,
            String id,
            Optional<String> verify_kind) throws PeerManagerException {
        PeerManager peerManager = context.getPeerManager();
        CollectiveIntermediary collective
                = peerManager.readCollectiveById(id);
        //todo-sv: verify kind with context
        return new ResidentCollective(context,
                id,
                null, //todo-sv figure this out 
                collective.getMembers(),null);
                //todo-sv: fix this collective.getAttributes());
    }

}
