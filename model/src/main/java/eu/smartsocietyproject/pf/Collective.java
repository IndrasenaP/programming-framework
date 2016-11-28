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

import java.util.Collection;
import java.util.Map;
import java.util.Optional;


public abstract class Collective {

    private final ApplicationContext context;
    private final String id;
    protected final CollectiveKind kind;
    private final ImmutableSet<Member> members;
    private final ImmutableMap<String, Attribute> attributes;


    protected Collective(
            ApplicationContext context,
            String id,
            CollectiveKind collectiveKind,
            Collection<Member> members,
            Map<String, ? extends Attribute> attributes) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(collectiveKind);
        Preconditions.checkNotNull(members);
        Preconditions.checkNotNull(attributes);
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
     * @return ImmutableSet<Member>
     */
    ImmutableSet<Member> getMembers() {
        return members;
    }

    ApplicationBasedCollective withMembers(Collection<Member> members) {
        return ApplicationBasedCollective.of(getContext(), getId(), getKindInstance(), members, getAttributes());
    }

    @Deprecated
    protected void setMembers(Collection<Member> newMembers) {
        throw new UnsupportedOperationException("Collective classes are now immutable");
    }

    
    public ImmutableMap<String, Attribute> getAttributes() {
        return attributes;
    }

    
    public Optional<Attribute> getAttribute(String name) {
        Attribute attribute = attributes.get(name);
        boolean test = attribute != null;
        return test ? Optional.of(attribute) : Optional.empty();
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
                .copy(toKind);

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
            return complement(primary, secondary, primary.getKind());
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
                .copy(toKind);

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


    public abstract ApplicationBasedCollective toApplicationBasedCollective();
    
    abstract Collective.WithVisibleMembers makeMembersVisible();
       
    public static abstract class WithVisibleMembers extends Collective {

        protected WithVisibleMembers(
                ApplicationContext context,
                String id,
                CollectiveKind collectiveKind,
                Collection<Member> members,
                Map<String, ? extends Attribute> attributes) {
            super(context, id, collectiveKind, members, attributes);

        }

        protected WithVisibleMembers(Collective c){
            super(c);
        }

        @Override
        public ApplicationBasedCollective withMembers(Collection<Member> members) {
            return super.withMembers(members);
        }

        @Override
        public ImmutableSet<Member> getMembers(){
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
                Collection<Member> members,
                Map<String, ? extends Attribute> attributes) {
            super(context, id, collectiveKind, members, attributes);

        }
        public Collective.WithVisibleMembers makeMembersVisible() {
            return this;
        }

        public ApplicationBasedCollective toApplicationBasedCollective(){
            return ApplicationBasedCollective
                    .of(this.getContext(), this.getId(), this.getKindInstance(), this.getMembers(), this.getAttributes());

        }
    }
}
