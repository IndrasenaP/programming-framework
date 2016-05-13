package eu.smartsocietyproject.pf.orchestration.orchestratorManagerAPI;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by zhenyuwen on 12/05/2016.
 */
public class testAPIs {
    public static void main(String [] args)throws IOException,JSONException {
        orchestratorAPI api=new ImpAPI();
      //  api.getPersonalTasks("luke1","luke1");
    //   api.getTask("luke1","luke1","http://localhost:3000/rideRequests/4");
    //    api.getPeronalPlans("luke1","luke1");
        api.getPlan("luke1","luke1","http://localhost:3000/ridePlans/0");
    }
}
