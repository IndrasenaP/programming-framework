package eu.smartsocietyproject.scenario3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.MoreObjects;
import eu.smartsocietyproject.pf.ApplicationException;
import eu.smartsocietyproject.pf.TaskDefinition;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.Optional;

public class S3TaskRequest extends TaskRequest {
    public static final String RequestFieldName = "request";
    public static final String ConversationFieldName = "conversation";
    public static final String PeerFieldName = "peer";

    private static final ObjectWriter writer = new ObjectMapper().writer();
    private final long deadline;
    private final JsonNode requestNode;
    private final String password;
    private final String peer;
    private final String conversation;

    public S3TaskRequest(TaskDefinition definition, String type) throws ApplicationException {
        super(definition, type);
        JsonNode origRequestNode = getDefinition().getJson().get(RequestFieldName);
        this.requestNode =
            Optional.ofNullable(origRequestNode)
                    .orElseThrow(() -> new ApplicationException(
                        String.format("Missing rideRequest field in: ", definition)));
        this.deadline =
            Optional.ofNullable(this.requestNode)
                    .flatMap(n->Optional.ofNullable(n.get("depDateTimeWindow")))
                    .flatMap(n->Optional.ofNullable(n.get("depDateTimeLow")))
                    .map(s -> s.asLong(0))
                    .orElseGet(() -> 0L);
        if ( deadline == 0L) {
            new ApplicationException(
                String.format("Unable to retrieve deadline from path .depDateTimeWindow.depDateTimeLow", definition));
        }

        this.password = getStringFromDefinition("password");
        this.conversation = getDefinition().getJson().get(ConversationFieldName).asText();
        this.peer = getDefinition().getJson().get(PeerFieldName).asText();
    }

    private String getStringFromDefinition(String fieldname) throws ApplicationException {
        JsonNode usernameJson = getDefinition().getJson().get(fieldname);

        if (usernameJson == null || usernameJson.isTextual()) {
            throw new ApplicationException(
                String.format("Missing or invalid [%s] field in: %s", fieldname, getDefinition()));
        }
        return usernameJson.asText();
    }

    public JsonNode getRideRequestNode() {
        return requestNode;
    }

    public long getDeadline() {
        return deadline;
    }

    @Override
    public String getRequest() {
        return "";
    }

    public String getPeer() {
        return peer;
    }

    public String getConversation() {
        return conversation;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        String json = "";
        try {
            json = writer.writeValueAsString(getRideRequestNode());
        } catch (JsonProcessingException e) {
            /* NOP */
        }
        return MoreObjects.toStringHelper(this.getClass().getName())
                          .add("id", getDefinition().getId())
                          .add("rideRequest", json)
                          .add("deadline", deadline)
                          .add("type", getType())
                          .toString();
    }
}
