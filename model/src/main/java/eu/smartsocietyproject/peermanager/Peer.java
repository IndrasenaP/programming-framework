package eu.smartsocietyproject.peermanager;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Peer {

    private final String id;
    private final String role;

    public Peer(String id, String role) {
        this.id = id;
        this.role = role;
    }

    public String getId() {
        return this.id;
    }

    /**
     * Role makes sense only within a collective. Unlike Peer attributes that are generally describing the peer as
     * entity, the role is the only 'special' attribute, describing the participation of the peer in a collective.
     * @return the name of the role
     */
    public String getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

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
