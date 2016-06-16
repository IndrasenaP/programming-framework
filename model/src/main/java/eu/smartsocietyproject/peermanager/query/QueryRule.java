/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.query;

import eu.smartsocietyproject.pf.Attribute;

/**
 * A QueryRule represents how to compare a given attribute value.
 * This especially means that the value behind the given attribute key will 
 * be compared to the value it has in this rule with the operation defined in 
 * this rule.
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class QueryRule {
    
    private String key;
    private Attribute attribute;
    private QueryOperation operation;
    
    public QueryRule withKey(String key) {
        this.key = key;
        return this;
    }
    
    public QueryRule withValue(Attribute attribute) {
        this.attribute = attribute;
        return this;
    }
    
    public QueryRule withOperation(QueryOperation operation) {
        this.operation = operation;
        return this;
    }
    
    public static QueryRule create(String key) {
        return new QueryRule().withKey(key);
    }

    public String getKey() {
        return key;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public QueryOperation getOperation() {
        return operation;
    }
}
