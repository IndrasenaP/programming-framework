package eu.smartsocietyproject.pf.orchestration.orchestratorManagerAPI;

import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.TaskRequest;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.Collection;

/**
 * Created by zhenyuwen on 26/04/2016.
 */
public class HttpClient {

    private Collective provisioned;
    private TaskRequest t;
    private  String path="http://localhost:3000";
    private static final String USER_AGENT = "Mozilla/5.0";
    public HttpClient(Collective provisioned, TaskRequest t){
        this.provisioned=provisioned;
        this.t=t;
     //   ResourceOfOM resource= new ResourceOfOM(provisioned);
    }

    private void  postRequest(){
        String target=path+"/rideRequests";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(target);
        Collection<Peer> peers= provisioned.getMembers();
        httpPost.addHeader("User-Agent", USER_AGENT);
    }
}
