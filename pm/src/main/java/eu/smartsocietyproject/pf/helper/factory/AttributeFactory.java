/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.helper.factory;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.helper.attributes.MembersAttribute;
import eu.smartsocietyproject.pf.helper.attributes.MongoMembersAttribute;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public interface AttributeFactory {
    MembersAttribute getMembersAttribute(JsonNode node) throws PeerManagerException;
}
