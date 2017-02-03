/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.adaptationPolicy;

import eu.smartsocietyproject.pf.CollectiveBasedTask;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public abstract class AdaptationPolicies {
    public static AbortPolicy abort() {
        return new AbortPolicy();
    }
    
    public static ExecutionAdaptationPolicy repeatExecution(int times) {
        return new RepeatXTimesPolicy(times, CollectiveBasedTask.State.EXECUTION);
    }
}
