package eu.smartsocietyproject.pf;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * An application managed collective, members are not accessible.
 * <p>
 * <p>
 * The class is immutable, new instances can be created by:
 * <ul>
 * <li>using the {@link ResidentCollective#toApplicationBasedCollective()}
 * method</li>
 * <li>using the set operations:
 * <ul>
 * <li>{@link ApplicationBasedCollective#intersection},</li>
 * <li>{@link ApplicationBasedCollective#union},</li>
 * <li>{@link ApplicationBasedCollective#difference}</li>

 * </ul>
 * <p>
 * While attributes must be consistent with the collective kind user attributes
 * can be completely free
 */
public final class ApplicationBasedCollective extends Collective {
    private ApplicationBasedCollective(
        ApplicationContext context,
        String id,
        CollectiveKind kind,
        Collection<Member> members,
        Map<String, ? extends Attribute> attributes) {
        super(context, id, kind, members, attributes);
    }

    /**
     * this methods creates an empty collective, mainly for tests
     * <p>
     * In the collective none of the current user attributes will be kept, only
     * the provided one will be present
     *
     * @param context the context to which the collective is associated
     * @param id the id of the collective
     * @param kind the kind of the collective
     * @return a new collective
     */
    public static ApplicationBasedCollective empty(ApplicationContext context, String id, String kind)
            throws CollectiveCreationException {

        return new ApplicationBasedCollective(
                context,
                id,
                getKindFromString(context, kind),
                ImmutableList.of(),
                ImmutableMap.of()
        );
    }

    static ApplicationBasedCollective of(
            ApplicationContext context,
            String id,
            CollectiveKind collectiveKind,
            Collection<Member> members,
            Map<String, ? extends Attribute> attributes) {
        Map<String, Attribute> newAttributes = collectiveKind.getDefaultValues();
        for (Map.Entry<String, ? extends Attribute> entry : attributes.entrySet()) {
            if (collectiveKind.isAttributeValid(entry.getKey(), entry.getValue())) {
                newAttributes.put(entry.getKey(), entry.getValue());
            }
        }
        return new ApplicationBasedCollective(
                context,
                id,
                collectiveKind,
                members,
                newAttributes
        );
    }

    private static CollectiveKind getKindFromString(ApplicationContext context, String kind)
            throws CollectiveCreationException {
        Optional<CollectiveKind> collectiveKind
                = context
                .getKindRegistry()
                .get(kind);

        if (!collectiveKind.isPresent()) {
            throw new CollectiveCreationException(String.format("Unknown collective kind: %s", kind));
        }

        return collectiveKind.get();
    }

    /**
     * this method creates a copy of the collective, changing the collective
     * kind
     * <p>
     * <p>
     * the copy created will contain all the user attributes of the original
     * collective, and all the attributes that have the same key of the new kind
     * will be copied only if the they are consistent with the new collective
     * kind, and will be ignored otherwise
     *
     * @param toKind
     * @return a collective of the new kind
     * @throws CollectiveCreationException when the new kind id is not known to
     * the current collective's context
     */
    public ApplicationBasedCollective copy(String toKind) throws CollectiveCreationException {
        CollectiveKind collectiveKind = getKindFromString(getContext(), toKind);

        if (collectiveKind.equals(getKindInstance())) {
            return this;
        }

        Map<String, Attribute> newAttributes = collectiveKind.getDefaultValues();

        for (Map.Entry<String, Attribute> entry : getAttributes().entrySet()) {
            if (collectiveKind.isAttributeValid(entry.getKey(), entry.getValue())) {
                newAttributes.put(entry.getKey(), entry.getValue());
            }
        }

        return new ApplicationBasedCollective(
                getContext(),
                getId(),
                collectiveKind,
                getMembers(),
                newAttributes
        );
    }

    /**
     * Since the class is immutable this method does not make any more sense is
     * it's not supported
     *
     * @param newAttributes
     * @throws UnsupportedOperationException always
     */
    @Deprecated
    public void setUserAttributes(Map<String, Attribute> newAttributes) {
        throw new UnsupportedOperationException("Collective classes now are immutable");
    }

    private ApplicationBasedCollective withAttribute(String key, Attribute value) throws CollectiveCreationException {
        return withAttributes(ImmutableMap.of(key, value));
    }

    ApplicationBasedCollective withAttributes(Map<String, ? extends Attribute> attributes)
            throws CollectiveCreationException {
        checkAttributes(attributes);
        return new ApplicationBasedCollective(
                this.getContext(),
                this.getId(),
                this.getKindInstance(),
                this.getMembers(),
                ImmutableMap.<String, Attribute>builder().putAll(this.getAttributes()).putAll(attributes).build()
        );
    }

    /**
     * this method returns a new collective which members will be the set
     * intersection of the current one (<i>this</i>) with the provided one
     * (<i>other</i>)
     * <p>
     * <p>
     * The attributes and user attributes will be merged giving precedence to
     * the current collective
     *
     * @param other
     * @return
     * @throws CollectiveCreationException when the collective kinds of the two
     * collective differ
     */
    public ApplicationBasedCollective intersection(ApplicationBasedCollective other)
            throws CollectiveCreationException {
        checkCompatibleKind(other);

        return
            other
                .withMembers(Sets.intersection(getMembers(), other.getMembers()))
                .withAttributes(this.getAttributes());
    }

    /**
     * this method returns a new collective which members will be the set
     * difference of the current one (<i>this</i>) with the provided one
     * (<i>other</i>)
     * <p>
     * <p>
     * The attributes and user attributes will be merged giving precedence to
     * the current collective
     *
     * @param other
     * @return
     * @throws CollectiveCreationException when the collective kinds of the two
     * collective differ
     */
    public ApplicationBasedCollective difference(ApplicationBasedCollective other)
            throws CollectiveCreationException {
        checkCompatibleKind(other);

        return other.withMembers(Sets.difference(this.getMembers(), other.getMembers())).
                withAttributes(this.getAttributes());
    }

    /**
     * this method returns a new collective which members will be the set union
     * of the current one (<i>this</i>) with the provided one (<i>other</i>)
     * <p>
     * <p>
     * The attributes and user attributes will be merged giving precedence to
     * the current collective
     *
     * @param other
     * @return
     * @throws CollectiveCreationException when the collective kinds of the two
     * collective differ
     */
    public ApplicationBasedCollective union(ApplicationBasedCollective other)
            throws CollectiveCreationException {
        checkCompatibleKind(other);

        return other.withMembers(Sets.union(this.getMembers(), other.getMembers())).
                withAttributes(this.getAttributes());
    }


    private void checkCompatibleKind(ApplicationBasedCollective other)
            throws CollectiveCreationException {
        if (!other.getKindInstance().equals(other.getKindInstance())) {
            throw new CollectiveCreationException("Set operations can be made only on the same kind");
        }
    }

    @Override
    public ApplicationBasedCollective toApplicationBasedCollective() {
        return this;
    }


    @Override
    public String toString() {
        return MoreObjects
                .toStringHelper(this)
                .add("context", getContext().getId())
                .add("id", getId())
                .add("kind", getKind())
                .add("members", "Unaccessible")
                .add("attributes", getAttributes())
                .toString();
    }


    public static ApplicationBasedCollective createFromQuery(
        ApplicationContext context,
        PeerQuery query) throws PeerManagerException {
        PeerManager peerManager = context.getPeerManager();
        return  peerManager.createCollectiveFromQuery(query);
    }

    public static ApplicationBasedCollective createFromQuery(
        ApplicationContext context,
        PeerQuery query,
        String kind) throws PeerManagerException {
        PeerManager peerManager = context.getPeerManager();
        return  peerManager.createCollectiveFromQuery(query, kind);
    }



    @Override
    protected Collective.WithVisibleMembers makeMembersVisible() {
        return new WithVisibleMembers();
    }

    private final class WithVisibleMembers extends Collective.WithVisibleMembers {

        private WithVisibleMembers(){
            super(ApplicationBasedCollective.this);
        }

        @Override
        public ApplicationBasedCollective toApplicationBasedCollective() {
            return ApplicationBasedCollective.this;
        }


        @Override
        protected WithVisibleMembers makeMembersVisible() {
            return this;
        }
    }


}
