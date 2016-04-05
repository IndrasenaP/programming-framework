package eu.smartsocietyproject.pf.orchestration.api;

import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.Plan;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.orchestration.CompositionException;

import java.util.HashMap;

/**
 * Created by zhenyuwen on 05/04/2016.
 */
public interface API {
    HashMap<Collective, Plan> OMCompose(Collective provisioned, TaskRequest t);
}