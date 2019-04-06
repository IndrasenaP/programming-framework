package eu.smartsocietyproject.runtime;


import eu.smartsocietyproject.payment.PaymentService;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.smartcom.SmartComService;

/** This class contains all the drivers to components needed by the platform
 *
 */
public class SmartSocietyComponents {
    private final PeerManager.Factory pmFactory;
    private final SmartComService.Factory scsFactory;
    private final PaymentService paymentService;

    public SmartSocietyComponents(PeerManager.Factory pmFactory,
            SmartComService.Factory scsFactory, PaymentService paymentService) {
        this.pmFactory = pmFactory;
        this.scsFactory = scsFactory;
        this.paymentService = paymentService;
    }

    PeerManager.Factory getPeerManagerFactory() {
        return pmFactory;
    }

    SmartComService.Factory getSmartComServiceFactory() {
        return scsFactory;
    }

    PaymentService getPaymentService() {
        return paymentService;
    }
}
