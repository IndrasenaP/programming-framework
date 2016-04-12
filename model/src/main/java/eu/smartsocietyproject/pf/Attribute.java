package eu.smartsocietyproject.pf;

public abstract class Attribute {
    private final Collective parent;

    public Attribute(Collective parent) {
        this.parent = parent;
    }

    public abstract Attribute clone(Collective newParent);

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
