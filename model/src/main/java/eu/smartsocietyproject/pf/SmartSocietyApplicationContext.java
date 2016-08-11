package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.peermanager.PeerManager;

import java.util.UUID;

public interface SmartSocietyApplicationContext {
    UUID getId();

    CollectiveKindRegistry getKindRegistry();

    PeerManager getPeerManager();

    boolean startTask(TaskDefinition definition);

    JsonNode monitor(UUID taskId);
}
