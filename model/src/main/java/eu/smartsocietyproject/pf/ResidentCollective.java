package eu.smartsocietyproject.pf;

import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.PeerQuery;

import java.util.Collection;
import java.util.Optional;

public final class ResidentCollective extends Collective {
    public ResidentCollective(SmartSocietyApplicationContext context, String id, String kind) {
        super(context, id, kind);
    }

    public static Collection<Peer> createFromQuery(
        SmartSocietyApplicationContext context,
        PeerQuery query) throws PeerManagerException {

        throw new UnsupportedOperationException("Still to be implemented");
    }

    public static Collection<Peer> createFromId(
        SmartSocietyApplicationContext context,
        String id,
        Optional<String> verify_kind) throws PeerManagerException {

        throw new UnsupportedOperationException("Still to be implemented");
    }

}
