package eu.smartsocietyproject.pf;

import com.google.common.base.MoreObjects;
import eu.smartsocietyproject.peermanager.PeerManager;

import java.util.UUID;

public class SmartSocietyApplicationContext {
    private final UUID id = java.util.UUID.randomUUID();
    private final CollectiveKindRegistry kindRegistry;
    private final PeerManager peerManager;

    public SmartSocietyApplicationContext(CollectiveKindRegistry kindRegistry, PeerManager peerManager) {
        this.kindRegistry = kindRegistry;
        this.peerManager = peerManager;
    }

    public UUID getId() {
        return id;
    }

    public CollectiveKindRegistry getKindRegistry() {
        return kindRegistry;
    }

    public PeerManager getPeerManager() {
        return peerManager;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("id", id)
                          .toString();
    }

}
