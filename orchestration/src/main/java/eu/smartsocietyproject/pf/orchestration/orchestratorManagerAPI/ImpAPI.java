package eu.smartsocietyproject.pf.orchestration.orchestratorManagerAPI;

import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.Plan;
import eu.smartsocietyproject.pf.TaskRequest;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by zhenyuwen on 05/04/2016.
 */
public class ImpAPI implements orchestratorAPI {
    HttpClient client= new HttpClient();

    public void postTaskRequest(String username, String password, String mode, int capcity, String dep, String dest) throws IOException {
        StringBuffer response=client.TaskRequest(username, password,  mode, capcity,  dep, dest) ;
    }
    public void getPersonalTasks(String user,String password)throws IOException{
        String dest="/rideRequests/?user="+user;
        JSONObject data =new JSONObject();
        StringBuffer response =client.sendGet(dest,user, password,data);
    }

    public void getTask (String user,String password,String taskId) throws  IOException{
        String taskPath="/rideRequests/:rideRequestID";
        JSONObject data =new JSONObject();
        StringBuffer response =client.sendGet(taskPath,user, password,data);
    }

    public void getPlan(String user,String password,String planId)throws  IOException{
        String planPath="/ridePlans/:ridePlanID";
        JSONObject data =new JSONObject();
        StringBuffer response =client.sendGet(planPath,user, password,data);
    }

}
