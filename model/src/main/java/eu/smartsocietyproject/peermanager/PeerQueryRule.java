/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager;

import eu.smartsocietyproject.pf.Attribute;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerQueryRule {
    
    private String key;
    private Attribute attribute;
    private PeerQueryOperation operation;
    
    public PeerQueryRule withKey(String key) {
        this.key = key;
        return this;
    }
    
    public PeerQueryRule withValue(Attribute attribute) {
        this.attribute = attribute;
        return this;
    }
    
    public PeerQueryRule withOperation(PeerQueryOperation operation) {
        this.operation = operation;
        return this;
    }
    
    public static PeerQueryRule create(String key) {
        return new PeerQueryRule().withKey(key);
    }

    public String getKey() {
        return key;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public PeerQueryOperation getOperation() {
        return operation;
    }
}
