package eu.smartsocietyproject.pf.orchestration.api;

import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.Plan;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.HashMap;

/**
 * Created by zhenyuwen on 05/04/2016.
 */
public class ImpAPI implements API {

   public HashMap<Collective, Plan> OMCompose(Collective provisioned, TaskRequest t){
       HashMap <Collective,Plan> result=new HashMap<Collective,Plan>();
       Collective c=new Collective();
       Plan p=new Plan();
       result.put(provisioned,p);
       return result;
    }
}