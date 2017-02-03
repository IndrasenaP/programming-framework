/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario2;

import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.scenario2.helper.RQATaskDefinition;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQATaskRequest extends TaskRequest {
    
    private int communityTime;
    private TimeUnit communityTimeUnit;
    private int orchestratorTime;
    private TimeUnit orchestratorUnit;

    public RQATaskRequest(RQATaskDefinition definition) {
        super(definition, "GoogleRequestTask");
    }

    @Override
    public String getRequest() {
        //since we know that it is a string node
        return getDefinition().getJson().get("question").asText();
    }

    @Override
    public RQATaskDefinition getDefinition() {
        return (RQATaskDefinition)super.getDefinition();
    }

    public int getCommunityTime() {
        return communityTime;
    }

    public void setCommunityTime(int communityTime) {
        this.communityTime = communityTime;
    }

    public TimeUnit getCommunityTimeUnit() {
        return communityTimeUnit;
    }

    public void setCommunityTimeUnit(TimeUnit communityTimeUnit) {
        this.communityTimeUnit = communityTimeUnit;
    }

    public int getOrchestratorTime() {
        return orchestratorTime;
    }

    public void setOrchestratorTime(int orchestratorTime) {
        this.orchestratorTime = orchestratorTime;
    }

    public TimeUnit getOrchestratorUnit() {
        return orchestratorUnit;
    }

    public void setOrchestratorUnit(TimeUnit orchestratorUnit) {
        this.orchestratorUnit = orchestratorUnit;
    }
    
}
