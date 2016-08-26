package eu.smartsocietyproject.runtime;


import eu.smartsocietyproject.peermanager.PeerManager;

/** This class contains all the drivers to components needed by the platform
 *
 */
public class SmartSocietyComponents {
    private final PeerManager peerManager;


    public SmartSocietyComponents(PeerManager peerManager) {
        this.peerManager = peerManager;
    }

    public PeerManager getPeerManager() {
        return peerManager;
    }
}
