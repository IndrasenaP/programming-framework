package eu.smartsocietyproject.pf;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import eu.smartsocietyproject.peermanager.PeerManager;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** The default implementation of the application context
 *
 */
public class SmartSocietyApplicationContext implements ApplicationContext {
    private final UUID uuid=UUID.randomUUID();
    private final CollectiveKindRegistry kindRegistry;
    private final PeerManager peerManager;
    private final ConcurrentHashMap<String, CBTBuilder> buildersByType = new ConcurrentHashMap<>();

    public SmartSocietyApplicationContext(CollectiveKindRegistry kindRegistry, PeerManager peerManager) {
        Preconditions.checkNotNull(kindRegistry);
        Preconditions.checkNotNull(peerManager);
        this.kindRegistry = kindRegistry;
        this.peerManager = peerManager;
    }

    /** Retrieve the registry of Collective kinds with attribute schema description
     * @return the registry of Collective kinds of type {@link CollectiveKindRegistry}
     * */
    @Override
    public CollectiveKindRegistry getKindRegistry() {
        return kindRegistry;
    }

    /** Register a {@link CBTBuilder} for a given type, several type might share the same builder, but
     * a builder cannot be registered to different contexts
     *
     * @param type task type
     * @param builder the {@link CBTBuilder} instance}
     * @return the {@link CBTBuilder} instance registered to the context
     * @exception IllegalStateException if the builder has been already registered to a different context
     * */
    @Override
    public CBTBuilder registerBuilderForCBTType(String type, CBTBuilder builder) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(builder);

        CBTBuilder registeredBuilder = builder.registerToContext(this);
        buildersByType.put(type, registeredBuilder);
        return registeredBuilder;
    }

    /** Retrieve a {@link CBTBuilder} for a given type
     *
     * @param type task type
     * @return the CBTBuilder instance registered to the context for the required type
     * @exception IllegalArgumentException when no builder has been registered for the given type
     * */
    @Override
    public CBTBuilder getCBTBuilder(String type) {
        Preconditions.checkNotNull(type);
        Preconditions.checkArgument(buildersByType.contains(type), "Unknown task type: %s", type);
        return buildersByType.get(type);
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    /** Return the peer manager associated to the context
     * @return an instance of class implementing the {@link PeerManager} interface
     * */
    @Override
    public PeerManager getPeerManager() {
        return peerManager;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("kindRegistry", kindRegistry)
                          .add("peerManager", peerManager)
                          .add("buildersByType", buildersByType)
                          .toString();
    }

}
