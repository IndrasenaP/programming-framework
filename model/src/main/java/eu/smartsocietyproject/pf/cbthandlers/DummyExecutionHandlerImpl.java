package eu.smartsocietyproject.pf.cbthandlers;

import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.TaskResult;

public class DummyExecutionHandlerImpl implements ExecutionHandler {

    private TaskResult myTask = new TaskResult();
    @Override
    public TaskResult execute(CollectiveWithPlan agreed) throws CBTLifecycleException {
        System.out.println("Doing some execution");
        try{Thread.sleep(200);}catch (InterruptedException ie){}
        System.out.println("Finished doing execution");
        return myTask;
    }

    @Override
    public double resultQoR(){
        return myTask.QoR();
    }
}

