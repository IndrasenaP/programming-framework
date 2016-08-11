package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.MoreObjects;
import eu.smartsocietyproject.peermanager.PeerManager;

import java.util.UUID;

public class DefaultSmartSocietyApplicationContext implements SmartSocietyApplicationContext {
    private final UUID id = java.util.UUID.randomUUID();
    private final CollectiveKindRegistry kindRegistry;
    private final PeerManager peerManager;

    public DefaultSmartSocietyApplicationContext(CollectiveKindRegistry kindRegistry, PeerManager peerManager) {
        this.kindRegistry = kindRegistry;
        this.peerManager = peerManager;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public CollectiveKindRegistry getKindRegistry() {
        return kindRegistry;
    }

    @Override
    public PeerManager getPeerManager() {
        return peerManager;
    }

    @Override
    public boolean startTask(TaskDefinition definition) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED YET"); // -=TODO=- (tommaso, 11/08/16)
    }

    @Override
    public JsonNode monitor(UUID taskId) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED YET"); // -=TODO=- (tommaso, 11/08/16)
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("id", id)
                          .add("kindRegistry", kindRegistry)
                          .add("peerManager", peerManager)
                          .toString();
    }

}
