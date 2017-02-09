/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario2;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import eu.smartsocietyproject.TaskResponse;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.CollectiveBasedTask;
import eu.smartsocietyproject.pf.SmartSocietyApplicationContext;
import eu.smartsocietyproject.pf.TaskFlowDefinition;
import eu.smartsocietyproject.pf.TaskResult;
import eu.smartsocietyproject.pf.adaptationPolicy.AdaptationPolicies;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQATaskRunnerVariantA extends RQATaskRunner {
    
    public RQATaskRunnerVariantA(RQATaskRequest request, SmartSocietyApplicationContext ctx) {
        super(request, ctx);
    }

    @Override
    public TaskResponse call() throws Exception {
        Collective nearbyPeers = this.queryNearbyPeers();
        
        TaskFlowDefinition tfd = this.getDefaultTaskFlowDefinition(nearbyPeers);
        
        request.setCommunityTime(2);
        request.setCommunityTimeUnit(TimeUnit.MINUTES);
        request.setOrchestratorTime(2);
        request.setOrchestratorUnit(TimeUnit.MINUTES);
        
        CollectiveBasedTask cbt = this.createCBT(tfd);
        cbt.start();
        
        TaskResult res = null;
        try{
            res = cbt.get(30, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            //incetivise: comentet out due to missing common peer manager
            //List<Long> times = new ArrayList<>();
            //times.add(new Long(2));
            //nearbyPeers.incentivize("mony", null, times);
        }
        
        if(res == null) {
            cbt = this.createCBT(tfd);
            cbt.start();
            
            res = cbt.get(5, TimeUnit.MINUTES);
        }
        
        if(res == null || !res.isQoRGoodEnough()) {
            return TaskResponse.FAIL;
        }
        
        this.sendResponse(res);
        
        return TaskResponse.OK;
    }
}
