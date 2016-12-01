/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario1.handler;

import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.scenario1.helper.RQAPlan;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.ResidentCollective;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.CompositionHandler;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQACompositionHandler implements CompositionHandler {
    public List<CollectiveWithPlan> compose(ApplicationContext context, 
            ApplicationBasedCollective provisioned, 
            TaskRequest t) throws CBTLifecycleException {
        try {
            //todo-sv: we should probably hide peermanager functions that return
            //residentCollectives into the internal peermanager interface
            //and provide initial queries that target resident collectives
            //but return ABCs
            ResidentCollective rc = context.getPeerManager()
                    .readCollectiveById(provisioned.getId().toString());
            
            //todo-sv: here we explicitly distinguish human and sw-peers but 
            //we could send it also to the whole collective at once
            Member swPeer = rc.getMembers().stream()
                    .filter(member -> member.getRole().equals("SWPeerForSearch"))
                    .findFirst().get();
            
            Collection<Member> humans = rc.getMembers().stream()
                    .filter(member -> !member.getRole().equals("SWPeerForSearch"))
                    .collect(Collectors.toList());
            
            RQAPlan plan = new RQAPlan(swPeer, humans, t);
            CollectiveWithPlan cwp = CollectiveWithPlan.of(provisioned, plan);
            return ImmutableList.of(cwp);
        } catch (PeerManagerException ex) {
            throw new CBTLifecycleException(ex);
        }
    }
    
}
