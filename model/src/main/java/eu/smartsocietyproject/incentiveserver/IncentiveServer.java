package eu.smartsocietyproject.incentiveserver;

import eu.smartsocietyproject.pf.Collective;

import java.util.List;


public interface IncentiveServer {

    /**
     *
     * Triggers the application of an incentive over a collective.
     *
     * @param collective The collective that will be the target of incentivization
     * @param incentiveType The type of incentive mechanism/scheme that will be applied
     * @param incentiveSpecificParams The parameters specific to the specified incentive type
     * @param times List of timestamps at which the incentives will be applied. If left null, the application is immediate.
     * @return true if incentive application successfully accepted for application by the underlying incentive server.
     * @throws IncentiveServerException In case the communication with the server failed, or the incentive could not
     * have been applied for any other reason.
     */
    boolean sendIncentive(Collective collective,
                          String incentiveType,
                          Object incentiveSpecificParams,
                          List<Long> times)
            throws IncentiveServerException;


}

