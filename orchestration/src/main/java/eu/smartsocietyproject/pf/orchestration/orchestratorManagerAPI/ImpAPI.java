package eu.smartsocietyproject.pf.orchestration.orchestratorManagerAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zhenyuwen on 05/04/2016.
 */
public class ImpAPI implements orchestratorAPI {
    HttpClient client= new HttpClient();

    public void postTaskRequest(String username, String password, String mode, int capcity, String dep, String dest) throws IOException {
        StringBuffer response=client.TaskRequest(username, password,  mode, capcity,  dep, dest) ;
    }
    public StringBuffer getPersonalTasks(String user,String password)throws IOException{
        String dest="/rideRequests/?user="+user;
        JSONObject data =new JSONObject();
        StringBuffer response =client.sendGet(dest,user, password,data);
       // System.out.println(response.toString());
        return response;
    }
    //  taskPath: http://localhost:3000/rideRequests/0
    public  StringBuffer getTask (String user,String password,String taskPath) throws  IOException{
        StringBuffer response =client.sendGet(taskPath,user, password);
        return response;
//        System.out.println(response.toString());
    }

    public JSONObject getPlan(String user,String password, String planURL)throws  IOException,JSONException{
        StringBuffer response =client.sendGet(planURL,user, password);
        return new JSONObject(response.toString());
    }

    public void getPeronalPlans(String user, String password) throws  IOException,JSONException{
        String path="/ridePlans/?action=getSet";
         JSONArray array=new JSONArray();
            ArrayList<String> urlSet= getPlanURLS(user,password);
         for (String URL:urlSet){
             JSONObject url =new JSONObject();
             url.put("url",URL);
             url.put("ETag" , "");
             url.put("doc"  , "");
             array.put(url);
         }
        JSONObject data =new JSONObject();
        data.put("data",array);
        StringBuffer response=client.sendPost(path, data, user,  password);
        System.out.println(response.toString());
    }

    // planTypes :"potentialRidePlans", "potentiallyAgreedRidePlans", "driverAgreedRidePlans", "agreedRidePlan", "invalidRidePlans"
    private ArrayList<String> getPlanURLS(String user,String password) throws IOException,JSONException{
        ArrayList<String> urls=new ArrayList<>();
        String planTypes="potentiallyAgreedRidePlans";
        JSONObject tasklinks=new JSONObject(getPersonalTasks(user,password).toString());
        JSONArray getUrls=tasklinks.getJSONArray("data");
        JSONArray urlSet=(JSONArray)getUrls.get(0);
        for (int a=0; a<urlSet.length();a++){
            String url=(String)urlSet.get(a);
            StringBuffer gettask=getTask(user,password,url);
            try {
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            JSONObject task=new JSONObject(gettask.toString());
            JSONArray taskPlanurls= task.getJSONArray(planTypes);
            for (int i=0; i<taskPlanurls.length();i++){
                urls.add((String)taskPlanurls.get(i));
            }

        }
        return urls;
    }

//    public void rejectPlan(){}
//    public void acceptPlan(String user, String password, String planId)throws IOException, JSONException{
//
//    }
}
