package eu.smartsocietyproject.DTO;

import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.TaskResult;
import eu.smartsocietyproject.pf.enummerations.State;

public class CollectiveBasedTaskDTO {
    private TaskRequest taskRequest;
    private TaskResult taskResult;
    private boolean cancelled;
    private boolean interrupted;
    private boolean executionException;
    private boolean running;
    private State state;
    private double qoR;
    private Collective inputCollective;

    public CollectiveBasedTaskDTO(TaskRequest taskRequest, TaskResult taskResult, boolean cancelled, boolean interrupted,
                                  boolean executionException, boolean running, State state, double qoR, Collective inputCollective) {
        this.taskRequest = taskRequest;
        this.taskResult = taskResult;
        this.cancelled = cancelled;
        this.interrupted = interrupted;
        this.executionException = executionException;
        this.running = running;
        this.state = state;
        this.qoR = qoR;
        this.inputCollective = inputCollective;
    }

    public TaskRequest getTaskRequest() {
        return taskRequest;
    }

    public TaskResult getTaskResult() {
        return taskResult;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public boolean isExecutionException() {
        return executionException;
    }

    public boolean isRunning() {
        return running;
    }

    public State getState() {
        return state;
    }

    public double getQoR() {
        return qoR;
    }

    public Collective getInputCollective() {
        return inputCollective;
    }
}
