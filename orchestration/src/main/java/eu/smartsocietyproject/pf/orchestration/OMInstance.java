package eu.smartsocietyproject.pf.orchestration;

import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.Plan;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.orchestration.orchestratorManagerAPI.orchestratorAPI;
import eu.smartsocietyproject.pf.orchestration.orchestratorManagerAPI.ImpAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhenyuwen on 05/04/2016.
 */
public class OMInstance {

    private orchestratorAPI api;
    public OMInstance(){

        this.api= new ImpAPI();
    }

    public List<CollectiveWithPlan> getPlans(Collective provisioned, TaskRequest t){
        List<CollectiveWithPlan> collectiveWithPlanList = new ArrayList<CollectiveWithPlan>();
        HashMap<Collective, Plan> componets= api.OMCompose(provisioned,t);
//        for(Collective c:componets.keySet()){
//            CollectiveWithPlan cwp=null;
//            collectiveWithPlanList.add(cwp.of(c,componets.get(c)));
//        }
        collectiveWithPlanList.add( CollectiveWithPlan.of(new Collective(), new Plan()));
        return collectiveWithPlanList;
    }

    public CollectiveWithPlan doNegotiate(CollectiveWithPlan cwp){
        return  CollectiveWithPlan.of(new Collective(), new Plan());
    }
}
