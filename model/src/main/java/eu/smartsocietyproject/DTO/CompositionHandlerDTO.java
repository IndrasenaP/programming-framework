package eu.smartsocietyproject.DTO;

import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.enummerations.State;

public class CompositionHandlerDTO {
    ApplicationBasedCollective applicationBasedCollective;

    public CompositionHandlerDTO(ApplicationBasedCollective applicationBasedCollective) {
        this.applicationBasedCollective = applicationBasedCollective;
    }

    public ApplicationBasedCollective getApplicationBasedCollective() {
        return applicationBasedCollective;
    }
}
