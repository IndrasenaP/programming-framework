/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager;

import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.CollectiveKind;
import eu.smartsocietyproject.pf.Member;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public interface CollectiveIntermediary {
    String getId();
    Collection<Member> getMembers();
    Map<String, Attribute> getAttributes(CollectiveKind kind);
}
