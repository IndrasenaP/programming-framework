package eu.smartsocietyproject.pf;


// TODO: Make this an abstract class, and have concrete scenarios implement subclasses
public class TaskResult {

    public String getResult() {
        return "abc";
    }
    public double QoR() {return 1.0;}
    public boolean isQoRGoodEnough(){ return QoR() > 0.5;}
}