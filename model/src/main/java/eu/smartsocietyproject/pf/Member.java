package eu.smartsocietyproject.pf;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Member {
    private String peerId;
    private String role;

    private Member(String peerId, String role) {
        this.peerId = peerId;
        this.role = role;
    }

    public String getPeerId() {
        return peerId;
    }

    public String getRole() {
        return role;
    }

    public static Member of(String peerId, String role) {
        return new Member(peerId, role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member that = (Member) o;

        return Objects.equal(this.peerId, that.peerId) &&
            Objects.equal(this.role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(peerId, role);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("peerId", peerId)
                          .add("role", role)
                          .toString();
    }

}
