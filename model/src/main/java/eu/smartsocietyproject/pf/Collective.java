/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;
import eu.smartsocietyproject.peermanager.query.CollectiveQuery;
import eu.smartsocietyproject.peermanager.query.PeerQuery;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public abstract class Collective {

    private final ApplicationContext context;
    private final String id;
    protected final CollectiveKind kind;
    private final ImmutableSet<Peer> members;
    private final ImmutableMap<String, Attribute> attributes;


    protected Collective(
            ApplicationContext context,
            String id,
            CollectiveKind collectiveKind,
            Collection<Peer> members,
            Map<String, ? extends Attribute> attributes) {
        this.context = context;
        this.id = id;
        this.kind = collectiveKind;
        this.members = ImmutableSet.copyOf(members);
        this.attributes = ImmutableMap.copyOf(attributes);
    }

    protected Collective(Collective c){
        this(c.context, c.id, c.kind, c.members, c.attributes);
    }

    public String getId() {
        return id;
    }

    protected ApplicationContext getContext() {
        return context;
    }

    
    public String getKind() {
        return kind.getId();
    }

    
    public CollectiveKind getKindInstance() {
        return kind;
    }

    /**
     * Return the members of this collective. This function is only for the
     * internal use public. Do not publish it to external developers.
     *
     * @return ImmutableSet<Peer>
     */
    public ImmutableSet<Peer> getMembers() {
        return members;
    }

    @Deprecated
    protected void setMembers(Collection<Peer> newMembers) {
        throw new UnsupportedOperationException("Collective classes are now immutable");
    }

    
    public ImmutableMap<String, Attribute> getAttributes() {
        return attributes;
    }

    
    public Optional<Attribute> getAttribute(String name) {
        Attribute attribute = attributes.get(name);
        return attribute != null
                ? Optional.of(attribute)
                : Optional.empty();
    }

    @Deprecated
    protected void setAttributes(Map<String, Attribute> newAttributes) {
        throw new UnsupportedOperationException("Collective class are now immutable");
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects
                .toStringHelper(this)
                .add("id", id)
                .add("kind", kind.getId())
                .add("members", members)
                .add("attributes", attributes);
    }

    protected void checkAttributes(Map<String, ? extends Attribute> attributes) throws CollectiveCreationException {
        for (Map.Entry<String, ? extends Attribute> entry : attributes.entrySet()) {
            if (!getKindInstance().isAttributeValid(entry.getKey(), entry.getValue())) {
                throw new CollectiveCreationException(
                        String.format(
                                "Attribute [%s] has a not valid value [%s] for kind [%s]",
                                entry.getKey(),
                                entry.getValue(),
                                getKind()));
            }
        }
    }

    
    public String toString() {
        return toStringHelper().toString();
    }

    
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Collective that = (Collective) o;

        return Objects.equal(this.id, that.id)
                && Objects.equal(this.kind, that.kind)
                && Objects.equal(this.context, that.context)
                && Objects.equal(this.members, that.members)
                && Objects.equal(this.attributes, that.attributes);
    }

    /**
     * Joins two collective members, favouring the primary collective w.r.t to
     * collective kind, attributes an user attributes
     *
     * @param primary the collective that will drive the choice of kind,
     * attributes (accordingly with the kind) and user attributes
     * @param secondary the collective that will only provide members to be
     * joined
     * @return A new collective which members is the union of the two
     * collective; the collective kind and attributes are those of the primary
     * collective; user attributes (if present) are taken from the primary
     * collective.
     */
    public static ApplicationBasedCollective join(Collective primary, Collective secondary) {

        try {
            return join(primary, secondary, Optional.empty());
        } catch (CollectiveCreationException e) {
            throw new IllegalStateException("Set operation without toKind failed", e);
        }
    }

    /**
     * Joins two collective members, changing the resulting collective kind to
     * the provided one, and favouring the primary collective w.r.t. attributes
     * and user attributes
     *
     * @param primary the collective that will drive the choice of attributes
     * (accordingly with the kind) and user attributes
     * @param secondary the collective that will only provide members to be
     * joined
     * @param toKind the resulting collective kind
     * @return A new collective which members is the union of the two
     * collective; the kind is the one provided; the attributes are those of the
     * primary collective that are consistent with the resulting kind, otherwise
     * collective kind defaults are used; user attributes (if present) are taken
     * from the primary collective.
     * @throws CollectiveCreationException if the toKind is not known to the
     * collective context
     */
    public static ApplicationBasedCollective join(Collective primary, Collective secondary, String toKind)
            throws CollectiveCreationException {
        Preconditions.checkNotNull(toKind);
        return join(primary, secondary, Optional.of(toKind));
    }

    /**
     * Joins two collective members, favouring the primary collective w.r.t to
     * collective kind, attributes an user attributes.
     *
     * @param primary the collective that will drive the choice of kind,
     * attributes (accordingly with the kind) and user attributes
     * @param secondary the collective that will only provide members to be
     * joined
     * @return A new collective which members is the union of the two
     * collective; if the optional collective kind is provided it will be used
     * instead of the primary collective kind; the attributes are those of the
     * primary collective that are consistent with the resulting kind, otherwise
     * collective kind defaults are used; user attributes (if present) are taken
     * from the primary collective.
     * @throws CollectiveCreationException if the toKind is provided but not
     * known
     */
    public static ApplicationBasedCollective join(
            Collective primary,
            Collective secondary,
            Optional<String> optionalKind)
            throws CollectiveCreationException {
        Preconditions.checkNotNull(primary);
        Preconditions.checkNotNull(secondary);
        Preconditions.checkNotNull(optionalKind);
        Preconditions.checkState(primary.getContext() == secondary.getContext());

        String toKind = optionalKind.orElse(primary.getKind());

        ApplicationBasedCollective primaryOperable
                = primary.toApplicationBasedCollective()
                .copy(toKind);
        ApplicationBasedCollective secondaryOperable
                = secondary.toApplicationBasedCollective()
                .copy(toKind)
                .withOnlyUserAttributes(ImmutableMap.of());

        primaryOperable = primaryOperable.copy(toKind);
        secondaryOperable = secondaryOperable.copy(toKind);

        return primaryOperable.intersection(secondaryOperable);
    }

    /**
     * It creates a new collective removing members of the secondary collective
     * from the primary ones, favouring the primary collective w.r.t to
     * collective kind, attributes an user attributes.
     *
     * @param primary the collective that will drive the choice of kind,
     * attributes and user attributes
     * @param secondary the collective that will only provide members to be
     * joined
     * @return A new collective which contains the members from the primary
     * collective that do not belong to the secondary; the collective kind and
     * attributes are those of the primary collective; user attributes (if
     * present) are taken from the primary collective.
     */
    public static ApplicationBasedCollective complement(Collective primary, Collective secondary) {

        try {
            return complement(primary, secondary, Optional.empty());
        } catch (CollectiveCreationException e) {
            throw new IllegalStateException("Set operation without toKind failed", e);
        }
    }

    /**
     * It creates a new collective removing members of the secondary collective
     * from the primary ones with the provided kind, favouring the primary
     * collective w.r.t to attributes an user attributes.
     *
     * @param primary the collective that will drive the choice of attributes
     * (accordingly with the kind) and user attributes
     * @param secondary the collective that will only provide members to be
     * joined
     * @return A new collective which contains the members from the primary
     * collective that do not belong to the secondary; the kind is the one
     * provided; the attributes are those of the primary collective that are
     * consistent with the resulting kind, otherwise collective kind defaults
     * are used; user attributes (if present) are taken from the primary
     * collective.
     */
    public static ApplicationBasedCollective complement(Collective primary, Collective secondary, String toKind)
            throws CollectiveCreationException {

        return complement(primary, secondary, Optional.empty());
    }

    /**
     * It creates a new collective removing members of the secondary collective
     * from the primary ones with the optional kind if provided otherwise using
     * the master ons, favouring the primary collective w.r.t to attributes an
     * user attributes.
     *
     * @param primary the collective that will drive the choice of kind,
     * attributes (accordingly with the kind) and user attributes
     * @param secondary the collective that will only provide members to be
     * joined
     * @return A new collective which containing the members from the primary
     * collective after removing those present also in the secondary; if the
     * optional collective kind is provided it will be used instead of the
     * primary collective kind; the attributes are those of the primary
     * collective that are consistent with the resulting kind, otherwise
     * collective kind defaults are used; user attributes (if present) are taken
     * from the primary collective.
     */
    public static ApplicationBasedCollective complement(
            Collective primary,
            Collective secondary,
            Optional<String> optionalKind)
            throws CollectiveCreationException {
        Preconditions.checkNotNull(primary);
        Preconditions.checkNotNull(secondary);
        Preconditions.checkNotNull(optionalKind);

        String toKind = optionalKind.orElse(primary.getKind());

        ApplicationBasedCollective primaryOperable
                = primary.toApplicationBasedCollective()
                .copy(toKind);
        ApplicationBasedCollective secondaryOperable
                = secondary.toApplicationBasedCollective()
                .copy(toKind)
                .withOnlyUserAttributes(ImmutableMap.of());

        return primaryOperable.intersection(secondaryOperable);
    }

    
    public int hashCode() {
        return Objects.hashCode(context, id, kind, members, attributes);
    }

    public static class CollectiveCreationException extends Exception {

        public CollectiveCreationException() {
        }

        public CollectiveCreationException(String message) {
            super(message);
        }

        public CollectiveCreationException(String message, Throwable cause) {
            super(message, cause);
        }

        public CollectiveCreationException(Throwable cause) {
            super(cause);
        }

        public CollectiveCreationException(
                String message,
                Throwable cause,
                boolean enableSuppression,
                boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    /**
     * Just used for internal debugging. Do not use. Will be removed.
     *
     * @return
     */
    @Deprecated
    public static Collective emptyCollective() {
        String basicKindId = "basic";
        CollectiveKind basicKind = CollectiveKind.builder(basicKindId).build();
        CollectiveKindRegistry kindRegistry = CollectiveKindRegistry.builder().register(basicKind).build();
        PeerManager peerManager = new PeerManager() {
            
            public void persistCollective(CollectiveIntermediary collective) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            
            public CollectiveIntermediary readCollectiveById(String id) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            
            public List<CollectiveIntermediary> readCollectiveByQuery(CollectiveQuery query) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            
            public CollectiveIntermediary readCollectiveByQuery(PeerQuery query) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        ApplicationContext context = new SmartSocietyApplicationContext(kindRegistry, peerManager);

        Collective c = null;
        try {
            c = ApplicationBasedCollective.empty(context, "theEmptyCollective", basicKindId);
        } catch (Exception e) {}
        return c;
    }

    public abstract ApplicationBasedCollective toApplicationBasedCollective();
    
    abstract Collective.WithVisibleMembers makeMembersVisible();
       
    public static abstract class WithVisibleMembers extends Collective {

        protected WithVisibleMembers(
                ApplicationContext context,
                String id,
                CollectiveKind collectiveKind,
                Collection<Peer> members,
                Map<String, ? extends Attribute> attributes) {
            super(context, id, collectiveKind, members, attributes);

        }
        protected WithVisibleMembers(Collective c){
            super(c);
        }
        public ImmutableSet<Peer> getMembers(){
            return super.getMembers();
        }
    }

    /**
     * To be used in tests to get access to members from ABC
     */
    public static class Testing extends WithVisibleMembers {
        protected Testing(
                ApplicationContext context,
                String id,
                CollectiveKind collectiveKind,
                Collection<Peer> members,
                Map<String, ? extends Attribute> attributes) {
            super(context, id, collectiveKind, members, attributes);

        }
        public Collective.WithVisibleMembers makeMembersVisible() {
            return this;
        }

        public ApplicationBasedCollective toApplicationBasedCollective(){
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
    }
}
