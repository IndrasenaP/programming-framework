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
public interface AdaptationPolicy {
    public CollectiveBasedTask.State adapt(Future currentFuture);
}
