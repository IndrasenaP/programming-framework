/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario2.helper;

import at.ac.tuwien.dsg.smartcom.model.Identifier;
import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.pf.TaskDefinition;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQATaskDefinition extends TaskDefinition {
    
    private final Identifier sender;
    private final boolean variantA;
    
    public RQATaskDefinition(JsonNode json, Identifier sender, boolean variantA) {
        super(json);
        this.sender = sender;
        this.variantA = variantA;
    }

    public Identifier getSender() {
        return sender;
    }

    public boolean isVariantA() {
        return variantA;
    }
}
