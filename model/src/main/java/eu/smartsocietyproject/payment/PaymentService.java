package eu.smartsocietyproject.payment;

import eu.smartsocietyproject.payment.smartcontracts.ICollectiveBasedTaskContract;
import eu.smartsocietyproject.payment.smartcontracts.ICollectiveBasedTaskContractFactory;
import eu.smartsocietyproject.pf.ResidentCollective;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

public interface PaymentService {
    CompletableFuture<TransactionReceipt> payCollective(ResidentCollective residentCollective, BigInteger price, ICollectiveBasedTaskContract collectiveBasedTaskContract);
    ICollectiveBasedTaskContract getDeployedContract(String address, ContractGasProvider gasProvider);
}
