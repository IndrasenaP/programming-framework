/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.adaptationPolicy;

import akka.actor.ActorRef;
import eu.smartsocietyproject.pf.CollectiveBasedTask;
import eu.smartsocietyproject.pf.enummerations.State;

import java.util.concurrent.Future;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RepeatXTimesPolicy implements ExecutionAdaptationPolicy {
    
    private int times;
    private State state;
    
    public RepeatXTimesPolicy(int x, State state) {
        this.times = x;
        this.state = state;
    }

    @Override
    public State adapt(ActorRef actorRef) {
        if(times<=0) {
            return State.FINAL;
        }
        
        this.times -= 1;
        return state;
    }
    
}
