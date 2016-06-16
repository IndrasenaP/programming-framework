/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.query;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract Query with the basic common query data.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public abstract class Query {
    private final List<QueryRule> rules;
    
    protected Query(){
        this.rules = new ArrayList<>();
    }
    
    public Query withRule(QueryRule rule) {
        this.rules.add(rule);
        return this;
    }
    
    public List<QueryRule> getQueryRules() {
        return this.rules;
    }
}
