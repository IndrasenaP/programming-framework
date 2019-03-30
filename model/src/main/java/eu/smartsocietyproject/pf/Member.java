package eu.smartsocietyproject.pf;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Member {
    private String peerId;
    private String role;
    private String address;

    private Member(String peerId, String role, String address) {
        this.peerId = peerId;
        this.role = role;
        this.address = address;
    }

    public String getPeerId() {
        return peerId;
    }

    public String getRole() {
        return role;
    }

    public String getAddress() {
        return address;
    }

    public static Member of(String peerId, String role, String address) {
        return new Member(peerId, role, address);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return java.util.Objects.equals(peerId, member.peerId) &&
                java.util.Objects.equals(role, member.role) &&
                java.util.Objects.equals(address, member.address);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(peerId, role, address);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("peerId", peerId)
                          .add("role", role)
                          .add("address", address)
                          .toString();
    }
}
