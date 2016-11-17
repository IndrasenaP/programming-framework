/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario1.handler;

import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.NegotiationHandler;
import java.util.List;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQANegotiationHandler implements NegotiationHandler {

    @Override
    public CollectiveWithPlan negotiate(ApplicationContext context, List<CollectiveWithPlan> negotiables) throws CBTLifecycleException {
        return negotiables.get(0);
    }
    
}
