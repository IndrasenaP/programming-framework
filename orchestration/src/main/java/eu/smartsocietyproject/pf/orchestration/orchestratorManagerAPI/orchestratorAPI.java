package eu.smartsocietyproject.pf.orchestration.orchestratorManagerAPI;


import java.io.IOException;

/**
 * Created by zhenyuwen on 05/04/2016.
 */
/*this interface describes the functionality of orchestrator manager */
public interface orchestratorAPI {
//   boolean authentication();
   public void postTaskRequest(String username, String password, String mode, int capcity, String dep, String dest) throws IOException;
   // get user's tasks (getriderequests)
   public void getPersonalTasks(String user,String password) throws IOException;

   public void getTask (String user,String password,String taskId) throws  IOException;
   public void getPlan(String user,String password,String planId)throws  IOException;

}