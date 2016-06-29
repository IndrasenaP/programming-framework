package eu.smartsocietyproject.pf.orchestration.orchestratorManagerAPI;

import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.pf.CollectiveBase;
import eu.smartsocietyproject.pf.ResidentCollective;
import eu.smartsocietyproject.pf.TaskRequest;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * Created by zhenyuwen on 26/04/2016.
 */
public class HttpClient {

//    private ResidentCollective provisioned;
//    private TaskRequest t;
    private  String path="http://localhost:3000";
    private static final String USER_AGENT = "Mozilla/5.0";
//    public HttpClient(ResidentCollective provisioned, TaskRequest t){
//        this.provisioned=provisioned;
//        this.t=t;
//     //   ResourceOfOM resource= new ResourceOfOM(provisioned);
//    }

    protected StringBuffer sendGet(String dest,String username, String password,JSONObject data) throws IOException{
        String target=path+dest;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(target);
        httpGet.addHeader("APP_KEY","DEV-WEB-CLIENT");
        httpGet.addHeader("APP_SECRET","6a019b20-d44c-11e3-9c1a-0800200c9a66");
        String basic="Basic " + username + ":" + password;
        httpGet.addHeader("Authorization",basic);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        System.out.println("GET Response Status:: "
                + httpResponse.getStatusLine().getStatusCode());

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpResponse.getEntity().getContent()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();

        httpClient.close();
        return response;
    }
    // this method is used to get the spesific task
    protected StringBuffer sendGet(String taskPath,String username, String password) throws IOException{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(taskPath);
        httpGet.addHeader("APP_KEY","DEV-WEB-CLIENT");
        httpGet.addHeader("APP_SECRET","6a019b20-d44c-11e3-9c1a-0800200c9a66");
        String basic="Basic " + username + ":" + password;
        httpGet.addHeader("Authorization",basic);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        System.out.println("GET Response Status:: "
                + httpResponse.getStatusLine().getStatusCode());

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpResponse.getEntity().getContent()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();

        httpClient.close();
        return response;
    }
    
    protected StringBuffer  sendPost(String dest, JSONObject data,String username, String password) throws IOException {
        String target=path+dest;
        System.out.println("url:"+target);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(target);
        httpPost.addHeader("APP_KEY","DEV-WEB-CLIENT");
        httpPost.addHeader("APP_SECRET","6a019b20-d44c-11e3-9c1a-0800200c9a66");
        String basic="Basic " + username + ":" + password;
        httpPost.addHeader("Authorization",basic);
        StringEntity se = new StringEntity(data.toString());
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httpPost.setEntity(se);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        System.out.println("POST Response Status:: "
                + httpResponse.getStatusLine().getStatusCode());

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpResponse.getEntity().getContent()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        httpClient.close();
        return response;
    }

//    protected StringBuffer sendPut(String dest, JSONObject data,String username, String password)throws IOException{
//        String target=dest;
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpPut httpPut=new HttpPut(target);
//        httpPut.addHeader("APP_KEY","DEV-WEB-CLIENT");
//        httpPut.addHeader("APP_SECRET","6a019b20-d44c-11e3-9c1a-0800200c9a66");
//        String basic="Basic " + username + ":" + password;
//        httpPut.addHeader("Authorization",basic);
//        StringEntity se = new StringEntity(data.toString());
//        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//        httpPut.setEntity(se);
//        CloseableHttpResponse httpResponse = httpClient.execute(httpPut);
//        System.out.println("PUT Response Status:: "
//                + httpResponse.getStatusLine().getStatusCode());
//        BufferedReader reader = new BufferedReader(new InputStreamReader(
//                httpResponse.getEntity().getContent()));
//
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = reader.readLine()) != null) {
//            response.append(inputLine);
//        }
//        reader.close();
//        httpClient.close();
//        return response;
//    }




//    protected  void Authentification(String username, String password){
//        try {
//            JSONObject auth=new JSONObject();
//
//            auth.put("APP_KEY","DEV-WEB-CLIENT");
//            auth.put("APP_SECRET","6a019b20-d44c-11e3-9c1a-0800200c9a66");
//            String basic="Basic " + username + ":" + password;
//            auth.put("Authorization",basic);
//
//        } catch (JSONException e){
//            System.out.println("unexpected JSON exception");
//        }
//    }
// when we get the implementation of task  class, this method has to be rewirten.
    protected StringBuffer TaskRequest(String username, String password, String mode, int capcity, String dep, String dest) throws IOException{
        String path="/rideRequests";
        try {
            JSONObject data =new JSONObject();
            data.put("user",username);
            data.put("potentialRidePlans",new JSONArray());
            data.put("potentiallyAgreedRidePlans",new JSONArray());
            data.put("driverAgreedRidePlans",new JSONArray());
            data.put("agreedRidePlan",new JSONArray());
            data.put("invalidRidePlans",new JSONArray());
            data.put("rideRecord","");
            data.put("mode",mode);
            data.put("currency","Euro");
            data.put("pets","No");
            data.put("smoking","No");
            data.put("rideQualityThreshold","5");
            data.put("capacity",capcity);
            data.put("rideRecord"," ");
            data.put("departureCity",dep);
            data.put("destinationCity",dest);
            data.put("desDateTimeWindow.desDateTimeLow","1396357200000");
            data.put("desDateTimeWindow.desDateTimeHigh","1396364400000");
            data.put("depDateTimeWindow.depDateTimeLow","1396350000000");
            data.put("depDateTimeWindow.depDateTimeHigh","1396353600000");
            data.put("priceBound","10");
            data.put("route","A route for agent"+username);
            data.put("comments","Agent"+username+"has generated this request");
            data.put("managedBy","");
            return sendPost(path, data, username, password);
        }catch (JSONException e){
            System.out.println("unexpected JSON exception");
        }
        return null;
    }

}
