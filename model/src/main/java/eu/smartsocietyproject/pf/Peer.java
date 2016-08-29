package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Peer {
    private final String id;
    private final JsonNode data;


    public Peer(String id) {
        this.id = id;
        this.data = new ObjectMapper().createObjectNode();
    }

    public Peer(String id, JsonNode data) {
        this.id = id;
        this.data = data.deepCopy();
    }

    public String getId() {
        return this.id;
    }

    /**
     *  This field is not actually used at the moment, and the way we store peer data
     *  might change.
     * @return
     */
    public JsonNode getData() {
        return this.data.deepCopy();
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

        return
            Objects.equal(this.id, that.id) &&
                Objects.equal(this.data, that.data)
            ;
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
                .add("data", data)
                .toString();
    }
}
