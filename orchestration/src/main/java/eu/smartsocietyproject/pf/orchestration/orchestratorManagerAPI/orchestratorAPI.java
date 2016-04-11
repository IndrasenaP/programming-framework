package eu.smartsocietyproject.pf.orchestration.orchestratorManagerAPI;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.Plan;
import eu.smartsocietyproject.pf.TaskRequest;


import java.util.HashMap;

/**
 * Created by zhenyuwen on 05/04/2016.
 */
public interface orchestratorAPI {
    HashMap<Collective, Plan> OMCompose(Collective provisioned, TaskRequest t);
}