package eu.smartsocietyproject.pf;

import eu.smartsocietyproject.peermanager.PeerManager;

import java.util.UUID;

/* Facade that allows the developer of a smartsociety application to interact to with the framework. In general we
 * expect that only a single Context is instantiated. This interface is mainly for allowing testing, but the only provided
 * implementation is {@link DefaultSmartSocietyApplicationContext}. */
public interface SmartSocietyApplicationContext {

    /** The context UUID
     *
     * @return a UUID
     */
    UUID getId();

    /** Return the peer manager associated to the context
     * @return an instance of class implementing the {@link PeerManager} interface
     * */
    PeerManager getPeerManager();

    /** Return the registry of Collective kinds with attribute schema description
     * @return the registry of Collective kinds of type {@link CollectiveKindRegistry}
     * */
    CollectiveKindRegistry getKindRegistry();

    /** Register a {@link CBTBuilder} for a given type, several type might share the same builder, but
     * a builder cannot be registered to different contexts
     *
     * @param type task type
     * @param builder the {@link CBTBuilder} instance}
     * @return the {@link CBTBuilder} instance registered to the context
     * @exception IllegalStateException if the builder has been already registered to a different context
     * */
    CBTBuilder registerBuilderForCBTType(String type, CBTBuilder builder);

    /** Retrieve a {@link CBTBuilder} for a given type
     *
     * @param type task type
     * @return the CBTBuilder instance registered to the context for the required type
     * @exception IllegalArgumentException when no builder has been registered for the given type
     * */
    CBTBuilder getCBTBuilder(String type);
}
