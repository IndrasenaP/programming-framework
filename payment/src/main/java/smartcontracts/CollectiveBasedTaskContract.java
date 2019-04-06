package smartcontracts;

import eu.smartsocietyproject.payment.smartcontracts.ICollectiveBasedTaskContract;
import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.1.1.
 */
public class CollectiveBasedTaskContract extends Contract implements ICollectiveBasedTaskContract {
    private static final String BINARY = "60806040526040516020806102e48339810180604052602081101561002357600080fd5b505160008054600160a060020a03909216600160a060020a031990921691909117905561028f806100556000396000f3fe608060405234801561001057600080fd5b5060043610610047577c010000000000000000000000000000000000000000000000000000000060003504635e514bce811461004c575b600080fd5b6100f16004803603604081101561006257600080fd5b81019060208101813564010000000081111561007d57600080fd5b82018360208201111561008f57600080fd5b803590602001918460208302840111640100000000831117156100b157600080fd5b91908080602002602001604051908101604052809392919081815260200183836020028082843760009201919091525092955050913592506100f3915050565b005b60005473ffffffffffffffffffffffffffffffffffffffff16331461017957604080517f08c379a000000000000000000000000000000000000000000000000000000000815260206004820152600e60248201527f6e6f7420617574686f72697a6564000000000000000000000000000000000000604482015290519081900360640190fd5b81518102303110156101ec57604080517f08c379a000000000000000000000000000000000000000000000000000000000815260206004820152601260248201527f696e73756666696369656e74206d6f6e65790000000000000000000000000000604482015290519081900360640190fd5b60005b825181101561025e57828181518110151561020657fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff166108fc839081150290604051600060405180830381858888f19350505050158015610255573d6000803e3d6000fd5b506001016101ef565b50505056fea165627a7a7230582071d010382b1bf6cc6f0b14670ad75e81d465d0200b2aee4c24e21b2ba27b42b10029";

    public static final String FUNC_PAYPEERS = "payPeers";

    public static final Event PRINT_EVENT = new Event("Print", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    @Deprecated
    protected CollectiveBasedTaskContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CollectiveBasedTaskContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected CollectiveBasedTaskContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected CollectiveBasedTaskContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> payPeers(List<String> peers, BigInteger price) {
        final Function function = new Function(
                FUNC_PAYPEERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(peers, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(price)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<PrintEventResponse> getPrintEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PRINT_EVENT, transactionReceipt);
        ArrayList<PrintEventResponse> responses = new ArrayList<PrintEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PrintEventResponse typedResponse = new PrintEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.sender = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<PrintEventResponse> printEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, PrintEventResponse>() {
            @Override
            public PrintEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PRINT_EVENT, log);
                PrintEventResponse typedResponse = new PrintEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.sender = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<PrintEventResponse> printEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PRINT_EVENT));
        return printEventFlowable(filter);
    }

    @Deprecated
    public static CollectiveBasedTaskContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CollectiveBasedTaskContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static CollectiveBasedTaskContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CollectiveBasedTaskContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static CollectiveBasedTaskContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new CollectiveBasedTaskContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static CollectiveBasedTaskContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new CollectiveBasedTaskContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<CollectiveBasedTaskContract> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, BigInteger initialWeiValue, String _smartSocietyOwner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_smartSocietyOwner)));
        return deployRemoteCall(CollectiveBasedTaskContract.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor, initialWeiValue);
    }

    public static RemoteCall<CollectiveBasedTaskContract> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, BigInteger initialWeiValue, String _smartSocietyOwner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_smartSocietyOwner)));
        return deployRemoteCall(CollectiveBasedTaskContract.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor, initialWeiValue);
    }

    @Deprecated
    public static RemoteCall<CollectiveBasedTaskContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, String _smartSocietyOwner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_smartSocietyOwner)));
        return deployRemoteCall(CollectiveBasedTaskContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    @Deprecated
    public static RemoteCall<CollectiveBasedTaskContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, String _smartSocietyOwner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_smartSocietyOwner)));
        return deployRemoteCall(CollectiveBasedTaskContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    public static class PrintEventResponse {
        public Log log;

        public String owner;

        public String sender;
    }
}
