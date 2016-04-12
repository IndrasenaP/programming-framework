package eu.smartsocietyproject.pf;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public final class CollectiveKindRegistry {
    private final Map<String, CollectiveKind> kinds;

    public CollectiveKindRegistry(Map<String, CollectiveKind> kinds) {
        this.kinds = kinds;
    }

    public CollectiveKind get(String kind) {
        CollectiveKind ret = kinds.get(kind);
        if ( kind == null ) {
            throw new NoSuchElementException(kind);
        }
        return ret;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, CollectiveKind> kinds = new HashMap<>();

        public Builder register(CollectiveKind kind) {
            kinds.put(kind.getId(), kind);
            return this;
        }

        public CollectiveKindRegistry build() {
            return new CollectiveKindRegistry(kinds);
        }
    }
}
