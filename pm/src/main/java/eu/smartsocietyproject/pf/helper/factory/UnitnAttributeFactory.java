/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.helper.factory;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.pf.helper.attributes.MongoMembersAttribute;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class UnitnAttributeFactory implements AttributeFactory {

    @Override
    public MongoMembersAttribute getMembersAttribute(JsonNode node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
