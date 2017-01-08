/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario2.helper;

import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.Plan;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.scenario2.RQATaskRequest;
import java.util.Collection;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQAPlan extends Plan {
    private Member google;
    private ApplicationBasedCollective humans;
    private Member orchestrator;
    private RQATaskRequest request;
    
    public RQAPlan(Member google, 
            ApplicationBasedCollective humans, 
            Member orchestrator,
            RQATaskRequest request) {
        this.google = google;
        this.humans = humans;
        this.orchestrator = orchestrator;
        this.request = request;
    }

    public Member getGoogle() {
        return google;
    }

    public ApplicationBasedCollective getHumans() {
        return humans;
    }

    public Member getOrchestrator() {
        return orchestrator;
    }

    public RQATaskRequest getRequest() {
        return request;
    }
}
