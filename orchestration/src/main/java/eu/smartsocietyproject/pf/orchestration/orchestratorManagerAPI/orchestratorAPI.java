package eu.smartsocietyproject.pf.orchestration.orchestratorManagerAPI;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.Plan;
import eu.smartsocietyproject.pf.TaskRequest;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.HashMap;

/**
 * Created by zhenyuwen on 05/04/2016.
 */
/*this interface describes the functionality of orchestrator manager */
public interface orchestratorAPI {
//   boolean authentication();
   public void postTaskRequest(String username, String password, String mode, int capcity, String dep, String dest) throws IOException;
   // get user's tasks (getriderequests)
   public StringBuffer getPersonalTasks(String user,String password) throws IOException;

   public  StringBuffer getTask (String user,String password,String taskId) throws  IOException;
   public JSONObject getPlan(String user,String password, String planURL)throws  IOException,JSONException;
//   public void deleteTask();
   // The following apis may not be used in the programming framework

   public void getPeronalPlans(String user, String password) throws  IOException,JSONException;

   // The following is for the negotiation, we need to discuss how to implement it.

//   public void rejectPlan();
//
//   public void acceptPlan(String user, String password, JSONObject plan)throws IOException, JSONException;



}