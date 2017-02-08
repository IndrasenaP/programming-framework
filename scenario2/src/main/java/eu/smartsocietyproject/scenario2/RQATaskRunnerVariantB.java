/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario2;

import eu.smartsocietyproject.TaskResponse;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.CollectiveBasedTask;
import eu.smartsocietyproject.pf.SmartSocietyApplicationContext;
import eu.smartsocietyproject.pf.TaskFlowDefinition;
import eu.smartsocietyproject.pf.TaskResult;
import eu.smartsocietyproject.pf.adaptationPolicy.AdaptationPolicies;
import eu.smartsocietyproject.scenario2.helper.RQATaskDefinition;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQATaskRunnerVariantB extends RQATaskRunner {

    public RQATaskRunnerVariantB(RQATaskRequest request, SmartSocietyApplicationContext ctx) {
        super(request, ctx);
    }

    @Override
    public TaskResponse call() throws Exception {
        Collective nearbyPeers = this.queryNearbyPeers();
        
        TaskFlowDefinition tfd = this.getDefaultTaskFlowDefinition(nearbyPeers)
                .withExecutionAdaptationPolicy(AdaptationPolicies
                        .repeatExecution(2));
        
        this.prepareRequest();
        
        CollectiveBasedTask cbt = this.createCBT(tfd);
        
        cbt.start();
        
        TaskResult res = cbt.get();
        
        if(!res.isQoRGoodEnough()) {
            return TaskResponse.FAIL;
        }
        
        this.sendResponse(res);
        
        return TaskResponse.OK;
    }
}
