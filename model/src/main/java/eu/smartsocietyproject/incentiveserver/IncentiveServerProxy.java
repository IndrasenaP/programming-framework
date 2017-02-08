package eu.smartsocietyproject.incentiveserver;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.smartsocietyproject.pf.Collective;

import java.util.List;
import java.util.stream.Collectors;


public class IncentiveServerProxy implements IncentiveServer{

    //TODO: The credentials should be read from the config.
    public static final String URL = "https://incentive.ise.bgu.ac.il/";
    private String user = "test1";
    private String pass = "123456";

    public boolean sendIncentive(Collective collective,
                                 String incentiveType,
                                 Object incentiveSpecificParams,
                                 List<Long> times) throws IncentiveServerException {

        String  postfix = "/sendIncentive/";

        String id = collective.getId();
        String messageToSend = incentiveSpecificParams.toString();
        String timestamps = "[]";

        if (times != null && times.size() > 0) {
            timestamps = times.stream()
                    .map(p -> "\"" + p + "\"")
                    .collect(Collectors.joining(", ", "[", "]"));
        }


        String body = "{\"project\": \"SmartShare\", " +
                "\"incentive_type\": \"" + incentiveType + "\", " +
                "\"incentive_text\": \"" + messageToSend + "\", " +
                "\"incentive_timestamp\":" + timestamps + ", " +
                "\"recipient\": { \"type\": \"collective\", \"id\": \"" + id +  "\"}}";

        HttpResponse<String> response;
        try {
            response =  Unirest.post(IncentiveServerProxy.URL + postfix)
                    .basicAuth(user, pass)
                    .header("accept", "text/plain")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asString();
        } catch (UnirestException urex) {
            throw new IncentiveServerException("Error contacting the Incentive Server.");
        }

        return 200 == response.getStatus() &&
                response.getBody().contains("Incentive") &&
                response.getBody().contains("Sent");

    }
}

