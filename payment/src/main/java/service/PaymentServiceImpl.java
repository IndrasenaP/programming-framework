package service;

import eu.smartsocietyproject.payment.PaymentService;
import eu.smartsocietyproject.payment.smartcontracts.ICollectiveBasedTaskContract;
import eu.smartsocietyproject.payment.smartcontracts.ICollectiveBasedTaskContractFactory;
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
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PaymentServiceImpl implements PaymentService {

    private Web3j web3j;
    private ContractGasProvider gasProvider;
    private Credentials credentials;
    private CompletableFuture<CollectiveBasedTaskContractFactory> cbtFactory;

    public PaymentServiceImpl(String ethereumNode, ContractGasProvider gasProvider, Credentials credentials,
                              BigInteger minimumPayment){

        this.gasProvider = gasProvider;
        this.credentials = credentials;
        this.web3j = Web3j.build(new HttpService(ethereumNode));
        this.cbtFactory =
                CollectiveBasedTaskContractFactory.deploy(web3j, credentials, gasProvider, minimumPayment).sendAsync();
    }

    @Override
    public CollectiveBasedTaskContract getDeployedContract(String address, ContractGasProvider gasProvider){
        try {
            boolean exists = cbtFactory.get().getCollectiveBasedTasks(address).send();
            if(exists)
                CollectiveBasedTaskContract.load(address, web3j, credentials, gasProvider);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CompletableFuture<TransactionReceipt> payCollective(ResidentCollective residentCollective, BigInteger price, ICollectiveBasedTaskContract collectiveBasedTaskContract){

        List<String> addresses = residentCollective.getMembers().stream()
                .map(Member::getAddress).collect(Collectors.toList());
          return collectiveBasedTaskContract.payPeers(addresses, price).sendAsync();
    }

    public CompletableFuture<CollectiveBasedTaskContractFactory>  getCbtFactory() {
        return cbtFactory;
    }
}
