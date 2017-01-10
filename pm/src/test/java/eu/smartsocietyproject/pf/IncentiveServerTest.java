package eu.smartsocietyproject.pf;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class IncentiveServerTest {


    @Ignore
    @Test
    public void testTriggerIncentive() throws IOException, UnirestException {

        HttpResponse<String> response =
                Unirest.post("https://incentive.ise.bgu.ac.il/sendIncentive/")
                        .basicAuth("test1", "123456")
                        .header("accept", "text/plain")
                        .header("Content-Type", "application/json")
                        .body("{\"project\": \"SmartShare\", " +
                                "\"incentive_type\": \"message\", " +
                                "\"incentive_text\": \"hello!\", " +
                                "\"incentive_timestamp\": [], " +
                                "\"recipient\": { \"type\": \"collective\", \"id\": \"6802\"}}")
                        .asString();

        int responseCode = response.getStatus();
        assertEquals(200, responseCode);
        String responseText = response.getBody();
        assert (responseText.contains("Incentive") && responseText.contains("Sent"));

    }


}
