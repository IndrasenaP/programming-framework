package eu.smartsocietyproject.peermanager;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public abstract class Peer {
    private final String id;

    public Peer(String id) {
        this.id = id;
    }
	
	protected String getId() {
		return this.id;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Peer that = (Peer) o;

        return Objects.equal(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


    @Override
    public String toString() {
        return MoreObjects
            .toStringHelper(this)
            .add("id", id)
            .toString();
    }
}
