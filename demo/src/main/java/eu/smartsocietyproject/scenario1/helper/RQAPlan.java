/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario1.helper;

import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.Plan;
import eu.smartsocietyproject.pf.TaskRequest;
import java.util.Collection;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQAPlan extends Plan {
    private Member google;
    private Collection<Member> humans;
    private TaskRequest request;
    
    public RQAPlan(Member google, Collection<Member> humans, TaskRequest request) {
        this.google = google;
        this.humans = humans;
        this.request = request;
    }

    public Member getGoogle() {
        return google;
    }

    public Collection<Member> getHumans() {
        return humans;
    }

    public TaskRequest getRequest() {
        return request;
    }
}
