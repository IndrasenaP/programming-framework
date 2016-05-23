/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

/**
 * This interface represents an atomic collective.
 * 
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public interface Collective {

	Optional<Attribute> getAttribute(String name);

	ImmutableMap<String, Attribute> getAttributes();

	String getId();

	String getKind();

	CollectiveKind getKindInstance();

	ApplicationBasedCollective toApplicationBasedCollective();
	
}
