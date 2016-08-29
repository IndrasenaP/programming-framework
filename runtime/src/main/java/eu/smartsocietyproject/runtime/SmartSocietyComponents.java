package eu.smartsocietyproject.runtime;


import eu.smartsocietyproject.peermanager.PeerManager;

/** This class contains all the drivers to components needed by the platform
 *
 */
public class SmartSocietyComponents {
    private final PeerManager.Factory pmFactory;

    public SmartSocietyComponents(PeerManager.Factory pmFactory) {
        this.pmFactory = pmFactory;
    }

    public PeerManager.Factory getPeerManagerFactory() {
        return pmFactory;
    }
}
