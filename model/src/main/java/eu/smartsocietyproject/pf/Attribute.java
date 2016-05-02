package eu.smartsocietyproject.pf;

public abstract class Attribute {
    public Attribute() {

    }

    public abstract Attribute clone();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
