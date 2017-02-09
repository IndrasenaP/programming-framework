/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.adaptationPolicy;

import eu.smartsocietyproject.pf.CollectiveBasedTask;
import java.util.concurrent.Future;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class AbortPolicy implements ExecutionAdaptationPolicy, 
        ProvisioningAdaptationPolicy, NegotiationAdaptationPolicy, 
        CompositionAdaptationPolicy {

    @Override
    public CollectiveBasedTask.State adapt(Future currentFuture) {
        return CollectiveBasedTask.State.FINAL;
    }
    
}
