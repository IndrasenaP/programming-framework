package eu.smartsocietyproject.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import eu.smartsocietyproject.scenario3.S3TaskRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RideSharingAPI {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private static final String RequestPath = "rideRequests";
    private static final String KeyHeader = "APP_KEY";
    private static final String SecretHeader = "APP_SECRET";
    private static final String PlanField = "agreedRidePlan";

    private final String key;
    private final String secret;
    private final WebTarget basicWebTarget;

    private final Pattern idRegex = Pattern.compile("/(.+)$");

    public RideSharingAPI(
        String url,
        String key,
        String secret) {
        this.key = key;
        this.secret = secret;
        Client client = ClientBuilder.newClient();
        basicWebTarget = client.target(url);
    }

    /* Send a request to the Orchestration Manager */
    public Optional<String> sendRequest(S3TaskRequest request) {
        try {
            Invocation.Builder invocationBuilder =
                basicWebTarget.path(RequestPath)
                              .request();
            Invocation invocation =
                addAuthenticationHeaders(invocationBuilder, request.getPeer(), request.getPassword())
                    .buildPost(Entity.entity(request.getRideRequestNode(), MediaType.APPLICATION_JSON_TYPE));
            JsonNode response = invocation.invoke(JsonNode.class);

            return Optional.of(response.get("data").asText()).map(x -> getIdFromUrl(x));
        } catch (ProcessingException | WebApplicationException ex) {
            log.error(String.format("Unable to submit request %s", request.getId()), ex);
            return Optional.empty();
        }
    }

    /* Try to retrieve from the Orchestration the planId that have been agreed upon, it returns an empty if
     * no agreement is reached before the deadline */
    public Optional<String> waitForPlan(S3TaskRequest request, String requestId, long pollingMs) {
        try {
            while (Instant.now().toEpochMilli() <= request.getDeadline()) {
                Optional<String> planId = getRequest(requestId, request.getPeer(), request.getPassword())
                    .map(this::getIdFromUrl);

                if (planId.isPresent())
                    return planId;

                try {
                    Thread.sleep(pollingMs);
                } catch (InterruptedException ignored) {
                }
            }
            return Optional.empty();
        } catch (ProcessingException | WebApplicationException ex) {
            log.error(String.format("Retrieve plan for ride sharing request %s", requestId), ex);
            return Optional.empty();
        }
    }

    private Optional<String> getRequest(String requestId, String username, String password) {
        Invocation.Builder invocationBuilder =
            basicWebTarget.path(RequestPath)
                          .path(requestId)
                          .request();
        Invocation invocation =
            addAuthenticationHeaders(invocationBuilder, username, password)
                .buildGet();
        JsonNode response = invocation.invoke(JsonNode.class);

        JsonNode n = response.get(PlanField);
        if (n == null)
            return Optional.empty();
        return Optional.ofNullable(Strings.emptyToNull(n.asText("")));
    }

    private String getIdFromUrl(String url) throws IllegalArgumentException {
        Matcher m = idRegex.matcher(url);
        if (!m.find()) {
            throw new IllegalArgumentException();
        }
        return m.group(1);
    }

    private Invocation.Builder addAuthenticationHeaders(
        Invocation.Builder invocationBuilder,
        String username,
        String password) {
        String authorizationString = String.format("Basic %s:%s", username, password);
        return
            invocationBuilder
                .header(KeyHeader, key)
                .header(SecretHeader, secret)
                .header(HttpHeaders.AUTHORIZATION, authorizationString);
    }


}
