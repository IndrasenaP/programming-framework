package eu.smartsocietyproject.payment.smartcontracts;

import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

public interface ICollectiveBasedTaskContract {
    RemoteCall<TransactionReceipt> payPeers(List<String> peers, BigInteger price);
}
