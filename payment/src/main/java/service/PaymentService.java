package service;

import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.ResidentCollective;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import smartcontracts.CollectiveBasedTaskContract;
import smartcontracts.CollectiveBasedTaskContractFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PaymentService {

    private Web3j web3j;
    private ContractGasProvider gasProvider;
    private Credentials credentials;
    private CompletableFuture<CollectiveBasedTaskContractFactory> cbtFactory;

    public PaymentService(String ethereumNode, ContractGasProvider gasProvider, Credentials credentials,
                          BigInteger minimumPayment){

        this.gasProvider = gasProvider;
        this.credentials = credentials;
        this.web3j = Web3j.build(new HttpService(ethereumNode));
        this.cbtFactory =
                CollectiveBasedTaskContractFactory.deploy(web3j, credentials, gasProvider, minimumPayment).sendAsync();
    }

    public CollectiveBasedTaskContract getDeployedContract(String address){
        return CollectiveBasedTaskContract.load(address, web3j, credentials, gasProvider);
    }

    public CompletableFuture<TransactionReceipt> payCollective(ResidentCollective residentCollective, BigInteger price, CollectiveBasedTaskContract collectiveBasedTaskContract){

        List<String> addresses = residentCollective.getMembers().stream()
                .map(Member::getAddress).collect(Collectors.toList());
          return collectiveBasedTaskContract.payPeers(addresses, price).sendAsync();
    }

    public CompletableFuture<CollectiveBasedTaskContractFactory>  getCbtFactory() {
        return cbtFactory;
    }
}
