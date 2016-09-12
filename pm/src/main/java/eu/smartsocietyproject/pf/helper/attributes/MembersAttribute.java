/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.helper.attributes;

import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.Member;
import java.security.KeyStore;
import java.util.List;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public interface MembersAttribute extends Attribute {
    List<Member> getMembers();
}
