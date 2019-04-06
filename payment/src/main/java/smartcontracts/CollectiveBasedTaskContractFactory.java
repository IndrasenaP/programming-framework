package smartcontracts;

import eu.smartsocietyproject.payment.smartcontracts.ICollectiveBasedTaskContractFactory;
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
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
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
public class CollectiveBasedTaskContractFactory extends Contract implements ICollectiveBasedTaskContractFactory {
    private static final String BINARY = "608060405234801561001057600080fd5b506040516020806106448339810180604052602081101561003057600080fd5b50516000811161008b576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252602a81526020018061061a602a913960400191505060405180910390fd5b60018054600160a060020a0319163317905560025561056b806100af6000396000f3fe608060405260043610610045577c0100000000000000000000000000000000000000000000000000000000600035046379b7a8d1811461004a578063e3a383911461009e575b600080fd5b34801561005657600080fd5b5061008a6004803603602081101561006d57600080fd5b503573ffffffffffffffffffffffffffffffffffffffff166100cf565b604080519115158252519081900360200190f35b6100a66100fa565b6040805173ffffffffffffffffffffffffffffffffffffffff9092168252519081900360200190f35b73ffffffffffffffffffffffffffffffffffffffff1660009081526020819052604090205460ff1690565b60025460009034101561016e57604080517f08c379a000000000000000000000000000000000000000000000000000000000815260206004820152601260248201527f696e73756666696369656e74206d6f6e65790000000000000000000000000000604482015290519081900360640190fd5b600154604051600091349173ffffffffffffffffffffffffffffffffffffffff9091169061019b9061024e565b73ffffffffffffffffffffffffffffffffffffffff9091168152604051908190036020019082f0801580156101d4573d6000803e3d6000fd5b5073ffffffffffffffffffffffffffffffffffffffff811660008181526020818152604091829020805460ff19166001179055815192835233908301523482820152519193508392507f2de4a2797894f5db2ffab1135ce9a206dc339041f1467e2c1a4640ec47a45d32919081900360600190a191505090565b6102e48061025c8339019056fe60806040526040516020806102e48339810180604052602081101561002357600080fd5b505160008054600160a060020a03909216600160a060020a031990921691909117905561028f806100556000396000f3fe608060405234801561001057600080fd5b5060043610610047577c010000000000000000000000000000000000000000000000000000000060003504635e514bce811461004c575b600080fd5b6100f16004803603604081101561006257600080fd5b81019060208101813564010000000081111561007d57600080fd5b82018360208201111561008f57600080fd5b803590602001918460208302840111640100000000831117156100b157600080fd5b91908080602002602001604051908101604052809392919081815260200183836020028082843760009201919091525092955050913592506100f3915050565b005b60005473ffffffffffffffffffffffffffffffffffffffff16331461017957604080517f08c379a000000000000000000000000000000000000000000000000000000000815260206004820152600e60248201527f6e6f7420617574686f72697a6564000000000000000000000000000000000000604482015290519081900360640190fd5b81518102303110156101ec57604080517f08c379a000000000000000000000000000000000000000000000000000000000815260206004820152601260248201527f696e73756666696369656e74206d6f6e65790000000000000000000000000000604482015290519081900360640190fd5b60005b825181101561025e57828181518110151561020657fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff166108fc839081150290604051600060405180830381858888f19350505050158015610255573d6000803e3d6000fd5b506001016101ef565b50505056fea165627a7a7230582071d010382b1bf6cc6f0b14670ad75e81d465d0200b2aee4c24e21b2ba27b42b10029a165627a7a72305820c06566dfef7dbc6fae1a4237b70a14ccc7b0e7b052bbb4175e3581014a1bb1630029546865206d696e696d756d2076616c75652073686f756c6420626520686967686572207468616e20302e";

    public static final String FUNC_GETCOLLECTIVEBASEDTASKS = "getCollectiveBasedTasks";

    public static final String FUNC_CREATECOLLECTIVEBASEDTASK = "createCollectiveBasedTask";

    public static final Event COLLECTIVEBASEDTASK_EVENT = new Event("CollectiveBasedTask", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected CollectiveBasedTaskContractFactory(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CollectiveBasedTaskContractFactory(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected CollectiveBasedTaskContractFactory(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected CollectiveBasedTaskContractFactory(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<Boolean> getCollectiveBasedTasks(String _address) {
        final Function function = new Function(FUNC_GETCOLLECTIVEBASEDTASKS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_address)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> createCollectiveBasedTask(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_CREATECOLLECTIVEBASEDTASK, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public List<CollectiveBasedTaskEventResponse> getCollectiveBasedTaskEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(COLLECTIVEBASEDTASK_EVENT, transactionReceipt);
        ArrayList<CollectiveBasedTaskEventResponse> responses = new ArrayList<CollectiveBasedTaskEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CollectiveBasedTaskEventResponse typedResponse = new CollectiveBasedTaskEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._address = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._from = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse._money = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<CollectiveBasedTaskEventResponse> collectiveBasedTaskEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, CollectiveBasedTaskEventResponse>() {
            @Override
            public CollectiveBasedTaskEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(COLLECTIVEBASEDTASK_EVENT, log);
                CollectiveBasedTaskEventResponse typedResponse = new CollectiveBasedTaskEventResponse();
                typedResponse.log = log;
                typedResponse._address = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._from = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse._money = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<CollectiveBasedTaskEventResponse> collectiveBasedTaskEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(COLLECTIVEBASEDTASK_EVENT));
        return collectiveBasedTaskEventFlowable(filter);
    }

    @Deprecated
    public static CollectiveBasedTaskContractFactory load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CollectiveBasedTaskContractFactory(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static CollectiveBasedTaskContractFactory load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CollectiveBasedTaskContractFactory(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static CollectiveBasedTaskContractFactory load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new CollectiveBasedTaskContractFactory(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static CollectiveBasedTaskContractFactory load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new CollectiveBasedTaskContractFactory(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<CollectiveBasedTaskContractFactory> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, BigInteger _minimum) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_minimum)));
        return deployRemoteCall(CollectiveBasedTaskContractFactory.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<CollectiveBasedTaskContractFactory> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, BigInteger _minimum) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_minimum)));
        return deployRemoteCall(CollectiveBasedTaskContractFactory.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<CollectiveBasedTaskContractFactory> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger _minimum) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_minimum)));
        return deployRemoteCall(CollectiveBasedTaskContractFactory.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<CollectiveBasedTaskContractFactory> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger _minimum) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_minimum)));
        return deployRemoteCall(CollectiveBasedTaskContractFactory.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class CollectiveBasedTaskEventResponse {
        public Log log;

        public String _address;

        public String _from;

        public BigInteger _money;
    }
}
