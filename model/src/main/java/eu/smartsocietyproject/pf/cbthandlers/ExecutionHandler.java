package eu.smartsocietyproject.pf.cbthandlers;

import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.TaskResult;

import java.util.List;

public interface ExecutionHandler{

    /* TODO:
     * This is just an approximate API. Depending on ow we will actually want to implement
     * obataining of results (blocking, callback, polling) we might change the API
     *
     * */

    TaskResult execute(ApplicationContext context, CollectiveWithPlan agreed) throws CBTLifecycleException;
    double resultQoR(); // returns [0-1]
    /**
     * Returns the TaskResult if it is good enough and otherwise null.
     * @return 
     */
    TaskResult getResultIfQoRGoodEnough();

    // boolean isDone(); // returns if the execution is done

}
