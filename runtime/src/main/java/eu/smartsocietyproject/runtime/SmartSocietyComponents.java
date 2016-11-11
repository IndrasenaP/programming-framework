package eu.smartsocietyproject.runtime;


import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.smartcom.SmartComService;

/** This class contains all the drivers to components needed by the platform
 *
 */
public class SmartSocietyComponents {
    private final PeerManager.Factory pmFactory;
    private final SmartComService.Factory scsFactory;

    public SmartSocietyComponents(PeerManager.Factory pmFactory,
            SmartComService.Factory scsFactory) {
        this.pmFactory = pmFactory;
        this.scsFactory = scsFactory;
    }

    public PeerManager.Factory getPeerManagerFactory() {
        return pmFactory;
    }

    public SmartComService.Factory getSmartComServiceFactory() {
        return scsFactory;
    }
}
