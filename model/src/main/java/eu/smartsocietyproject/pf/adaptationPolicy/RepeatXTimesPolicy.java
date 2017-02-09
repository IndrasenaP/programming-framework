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
public class RepeatXTimesPolicy implements ExecutionAdaptationPolicy {
    
    private int times;
    private CollectiveBasedTask.State state;
    
    public RepeatXTimesPolicy(int x, CollectiveBasedTask.State state) {
        this.times = x;
        this.state = state;
    }

    @Override
    public CollectiveBasedTask.State adapt(Future currentFuture) {
        if(times<=0) {
            return CollectiveBasedTask.State.FINAL;
        }
        
        this.times -= 1;
        return state;
    }
    
}
