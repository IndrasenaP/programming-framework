package eu.smartsocietyproject.pf;


public abstract class TaskResult {
    public abstract String getResult();
    public abstract double QoR();
    public abstract boolean isQoRGoodEnough();
}