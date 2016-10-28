/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.demo.handler;

import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ProvisioningHandler;
import java.util.Optional;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class QAProvisioningHandler implements ProvisioningHandler{

    public ApplicationBasedCollective provision(ApplicationContext context, TaskRequest t, Optional<Collective> inputCollective) throws CBTLifecycleException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
