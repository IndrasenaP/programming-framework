package eu.smartsocietyproject.pf;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.smartsocietyproject.peermanager.Peer;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class Collective {
    private final SmartSocietyApplicationContext context;
    private final String id;
    protected final String kind;
    private Set<Peer> members = ImmutableSet.of();
    private Map<String, Attribute> attributes = ImmutableMap.of();

    public Collective(SmartSocietyApplicationContext context, String id, String kind) {
        this.context = context;
        this.id = id;
        this.kind = kind;
    }

    protected Collective(Collective from, Optional<String> kind) {
        context = from.getContext();
        id = from.getId();
        this.kind = kind.orElse(from.getKind());
        setMembers(from.getMembers());
        setAttributes(from.getAttributes());
    }

    public String getId() {
        return id;
    }

    protected SmartSocietyApplicationContext getContext() {
        return context;
    }

    public String getKind() {
        return kind;
    }

    protected Collection<Peer> getMembers() {
        return Lists.newArrayList(members);
    }

    protected void setMembers(Collection<Peer> newMembers) {
        members = ImmutableSet.copyOf(newMembers);
    }

    protected Map<String, Attribute> getAttributes() {
        return Maps.newHashMap(attributes);
    }

    protected void setAttributes(Map<String, Attribute> newAttributes) {
        attributes = ImmutableMap.copyOf(newAttributes);
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects
            .toStringHelper(this)
            .add("context", context)
            .add("id", id)
            .add("kind", kind)
            .add("members", members)
            .add("attributes", attributes);
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Collective that = (Collective) o;

        return
            Objects.equal(this.id, that.id) &&
                Objects.equal(this.kind, that.kind) &&
                Objects.equal(this.context, that.context) &&
                Objects.equal(this.members, that.members) &&
                Objects.equal(this.attributes, that.attributes);
    }

    @Override
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
}
