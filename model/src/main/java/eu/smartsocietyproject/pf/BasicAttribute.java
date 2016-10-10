/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public abstract class BasicAttribute<T> implements Attribute {
    private T value;
    private final AttributeType type;
    protected BasicAttribute(AttributeType type, T value) {
        this.type = type;
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }
    
    public AttributeType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        //todo-sv discuss writing the Attribute classes instead of dynamically creating them
        //Since we are always creating new BasicAttribute classes in
        //AttributeType.from we can not simply compare the classes of two 
        //attributes they will never be the same.
        if (o == null || !(o instanceof BasicAttribute)) return false;

        BasicAttribute that = (BasicAttribute) o;

        return
            Objects.equal(this.value, that.value) &&
            Objects.equal(this.getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getType(), value);
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("value", value)
                          .add("type", getType())
                          .toString();
    }
}
