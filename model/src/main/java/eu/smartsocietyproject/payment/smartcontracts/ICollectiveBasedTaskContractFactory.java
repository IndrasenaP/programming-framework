package eu.smartsocietyproject.payment.smartcontracts;

import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

public interface ICollectiveBasedTaskContractFactory {
    RemoteCall<Boolean> getCollectiveBasedTasks(String _address);
    RemoteCall<TransactionReceipt> createCollectiveBasedTask(BigInteger weiValue);
}
