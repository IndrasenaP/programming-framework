package eu.smartsocietyproject.pf;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.UUID;

public class SmartSocietyApplicationContext {
    UUID id = java.util.UUID.randomUUID();
    CollectiveKindRegistry kindRegistry;

    public SmartSocietyApplicationContext(CollectiveKindRegistry kindRegistry) {
        this.kindRegistry = kindRegistry;
    }

    public UUID getId() {
        return id;
    }

    public CollectiveKindRegistry getKindRegistry() {
        return kindRegistry;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("id", id)
                          .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmartSocietyApplicationContext that = (SmartSocietyApplicationContext) o;

        return Objects.equal(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


}
