/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.demo.handler;

import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.ResidentCollective;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ProvisioningHandler;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQAProvisioningHandler implements ProvisioningHandler{

    public ApplicationBasedCollective provision(ApplicationContext context, 
            TaskRequest t, 
            Optional<Collective> inputCollective) throws CBTLifecycleException {
        try {
            //todo-sv: more detailed see document
            ApplicationBasedCollective abc = inputCollective.get()
                    .toApplicationBasedCollective();
            //persisting collective so that composition handler can
            //have access to the different members
            context.getPeerManager().persistCollective(abc);
            return abc;
        } catch (PeerManagerException ex) {
            throw new CBTLifecycleException(ex);
        }
    }
    
}
