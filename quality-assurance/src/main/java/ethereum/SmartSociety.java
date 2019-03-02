package ethereum;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
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
public class SmartSociety extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b5060018054600160a060020a03191633179055611114806100326000396000f3fe608060405260043610610045577c01000000000000000000000000000000000000000000000000000000006000350463737c6f20811461004a578063ee3dbb0b14610231575b600080fd5b6101bc600480360360e081101561006057600080fd5b81019060208101813564010000000081111561007b57600080fd5b82018360208201111561008d57600080fd5b803590602001918460208302840111640100000000831117156100af57600080fd5b919390928235926040810190602001356401000000008111156100d157600080fd5b8201836020820111156100e357600080fd5b8035906020019184602083028401116401000000008311171561010557600080fd5b9193909282359260408101906020013564010000000081111561012757600080fd5b82018360208201111561013957600080fd5b8035906020019184602083028401116401000000008311171561015b57600080fd5b9193909282359260408101906020013564010000000081111561017d57600080fd5b82018360208201111561018f57600080fd5b803590602001918460208302840111640100000000831117156101b157600080fd5b509092509050610296565b6040805160208082528351818301528351919283929083019185019080838360005b838110156101f65781810151838201526020016101de565b50505050905090810190601f1680156102235780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561023d57600080fd5b50610246610549565b60408051602080825283518183015283519192839290830191858101910280838360005b8381101561028257818101518382015260200161026a565b505050509050019250505060405180910390f35b60015460405160609160009134918691869173ffffffffffffffffffffffffffffffffffffffff16906102c8906105b8565b80806020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018281038252858582818152602001925060200280828437600083820152604051601f909101601f19169092018290039650909450869350505050f08015801561034c573d6000803e3d6000fd5b50905090508073ffffffffffffffffffffffffffffffffffffffff1663f1df73ae8e8e8e8e8e8e8e8e8e6040518a63ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018080602001898152602001806020018781526020018060200185815260200184810384528d8d82818152602001925060200280828437600083820152601f01601f191690910185810384528a8152602090810191508b908b0280828437600083820152601f01601f19169091018581038352878152602090810191508890880280828437600081840152601f19601f8201169050808301925050509c505050505050505050505050506040805180830381600087803b15801561046657600080fd5b505af115801561047a573d6000803e3d6000fd5b505050506040513d604081101561049057600080fd5b5050600080546001810182559080527f290decd9548b62a8d60345a988386fc84ba6bc95484008f6362f93160ef3e56301805473ffffffffffffffffffffffffffffffffffffffff831673ffffffffffffffffffffffffffffffffffffffff1990911681179091556040805191825233602083015234828201525182917fc55bc97fb695344e611c6cff8a085f51a747bef1e4607940f158d6c3639766a3919081900360600190a150509b9a5050505050505050505050565b606060008054806020026020016040519081016040528092919081815260200182805480156105ae57602002820191906000526020600020905b815473ffffffffffffffffffffffffffffffffffffffff168152600190910190602001808311610583575b5050505050905090565b610b23806105c68339019056fe60806040819052600a805460ff19166001179055610b23388190039081908339810180604052604081101561003357600080fd5b81019080805164010000000081111561004b57600080fd5b8201602081018481111561005e57600080fd5b815185602082028301116401000000008211171561007b57600080fd5b5050602091909101516002805433600160a060020a03199182161790915560018054909116600160a060020a038316179055909250905060005b825181101561013c5760006060604051908101604052806000151581526020016000815260200185848151811015156100ea57fe5b6020908102919091018101519091528254600180820185556000948552938290208351600390920201805460ff19169115159190911781559082015181840155604090910151600290910155016100b5565b5050506109d58061014e6000396000f3fe608060405234801561001057600080fd5b50600436106100af576000357c010000000000000000000000000000000000000000000000000000000090048063966dae0e11610083578063966dae0e146101b0578063de292789146101b8578063e1e29558146101de578063f1df73ae146101fb578063f8a8fd6d1461033d576100af565b80622f8c57146100b45780635e514bce146100d85780638926078f1461017f5780638a6655d614610187575b600080fd5b6100bc610357565b60408051600160a060020a039092168252519081900360200190f35b61017d600480360360408110156100ee57600080fd5b81019060208101813564010000000081111561010957600080fd5b82018360208201111561011b57600080fd5b8035906020019184602083028401116401000000008311171561013d57600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295505091359250610366915050565b005b61017d6103e7565b61017d6004803603606081101561019d57600080fd5b50803590602081013590604001356104c1565b6100bc6105cb565b6101c06105da565b60408051938452602084019290925282820152519081900360600190f35b61017d600480360360208110156101f457600080fd5b5035610640565b610317600480360360c081101561021157600080fd5b81019060208101813564010000000081111561022c57600080fd5b82018360208201111561023e57600080fd5b8035906020019184602083028401116401000000008311171561026057600080fd5b9193909282359260408101906020013564010000000081111561028257600080fd5b82018360208201111561029457600080fd5b803590602001918460208302840111640100000000831117156102b657600080fd5b919390928235926040810190602001356401000000008111156102d857600080fd5b8201836020820111156102ea57600080fd5b8035906020019184602083028401116401000000008311171561030c57600080fd5b919350915035610693565b60408051600160a060020a03938416815291909216602082015281519081900390910190f35b610345610779565b60408051918252519081900360200190f35b600154600160a060020a031681565b600154600160a060020a0316331461037d57600080fd5b60005b82518110156103e257828181518110151561039757fe5b90602001906020020151600160a060020a03166108fc839081150290604051600060405180830381858888f193505050501580156103d9573d6000803e3d6000fd5b50600101610380565b505050565b600154600160a060020a031633146103fe57600080fd5b6000546009541461040e57600080fd5b600a805460ff191690556000805411156104365761043660008060016000805490500361079a565b7f3b969cf9821ede6f28f32f07aecb234fcf0c68a31553e7afc474e6f3a15209c960008081548110151561046657fe5b90600052602060002090600302016002015460008081548110151561048757fe5b906000526020600020906003020160010154600c5460405180848152602001838152602001828152602001935050505060405180910390a1565b600154600160a060020a031633146104d857600080fd5b60005482108015610507575060008054839081106104f257fe5b600091825260209091206003909102015460ff165b80156105155750600a5460ff165b151561052057600080fd5b6003548310801561054057506000838152600b602052604090205460ff16155b151561054b57600080fd5b8060001115801561055d575080600110155b151561056857600080fd5b600c805460019081019091556000848152600b60205260408120805460ff191690921790915580548291908490811061059d57fe5b6000918252602090912060039182020160010180549290920190915554600c5414156103e2576103e26103e7565b600254600160a060020a031681565b600a546000908190819060ff16156105f157600080fd5b60008054819081106105ff57fe5b906000526020600020906003020160020154925060008081548110151561062257fe5b9060005260206000209060030201600101549150600c549050909192565b600154600160a060020a0316331461065757600080fd5b600160008281548110151561066857fe5b60009182526020909120600390910201805460ff191691151591909117905550600980546001019055565b6002546000908190600160a060020a0316331461071157604080517f08c379a000000000000000000000000000000000000000000000000000000000815260206004820152600c60248201527f7765206265656e206b6e65770000000000000000000000000000000000000000604482015290519081900360640190fd5b61071d60038c8c610908565b5061072a60048989610908565b5061073760058686610908565b5060068990556007869055600883905530318a8a028888020185850201111561075c57fe5b5050600154600160a060020a031633995099975050505050505050565b600154600090600160a060020a0316331461079357600080fd5b5030315b90565b818160008560028484030486018154811015156107b357fe5b90600052602060002090600302016001015490505b8183116108da575b8086848154811015156107df57fe5b9060005260206000209060030201600101541015610802576001909201916107d0565b858281548110151561081057fe5b9060005260206000209060030201600101548110156108355760001990910190610802565b8183116108d557858281548110151561084a57fe5b906000526020600020906003020160010154868481548110151561086a57fe5b906000526020600020906003020160010154878581548110151561088a57fe5b9060005260206000209060030201600101600089868154811015156108ab57fe5b60009182526020909120600160039092020181019390935550919091559290920191600019909101905b6107c8565b818510156108ed576108ed86868461079a565b838310156109005761090086848661079a565b505050505050565b828054828255906000526020600020908101928215610968579160200282015b8281111561096857815473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a03843516178255602090920191600190910190610928565b50610974929150610978565b5090565b61079791905b8082111561097457805473ffffffffffffffffffffffffffffffffffffffff1916815560010161097e56fea165627a7a7230582007c8ec35dda19e56257d3292ad23d4adbaeed2509a0757bd6b9ee624d8c2a1b10029a165627a7a723058206a2d30f0bab421125bfa7ee60b35954d3c2b4a088c9fb129c1344eabbb80eb140029";

    public static final String FUNC_CREATESMARTSOCIETYAPPLICATION = "createSmartSocietyApplication";

    public static final String FUNC_GETSMARTSOCIETYAPPLICATIONS = "getSmartSocietyApplications";

    public static final Event SMARTSOCIETYAPP_EVENT = new Event("SmartSocietyApp", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected SmartSociety(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SmartSociety(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected SmartSociety(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected SmartSociety(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> createSmartSocietyApplication(List<String> voters, BigInteger voterPrice, List<String> workers, BigInteger workerPrice, List<String> substitutes, BigInteger substitutePrice, List<byte[]> _tasks, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_CREATESMARTSOCIETYAPPLICATION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(voters, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(voterPrice), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(workers, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(workerPrice), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(substitutes, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(substitutePrice), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.Utils.typeMap(_tasks, org.web3j.abi.datatypes.generated.Bytes32.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<List> getSmartSocietyApplications() {
        final Function function = new Function(FUNC_GETSMARTSOCIETYAPPLICATIONS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteCall<List>(
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public List<SmartSocietyAppEventResponse> getSmartSocietyAppEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SMARTSOCIETYAPP_EVENT, transactionReceipt);
        ArrayList<SmartSocietyAppEventResponse> responses = new ArrayList<SmartSocietyAppEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SmartSocietyAppEventResponse typedResponse = new SmartSocietyAppEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._address = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._from = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse._money = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SmartSocietyAppEventResponse> smartSocietyAppEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, SmartSocietyAppEventResponse>() {
            @Override
            public SmartSocietyAppEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SMARTSOCIETYAPP_EVENT, log);
                SmartSocietyAppEventResponse typedResponse = new SmartSocietyAppEventResponse();
                typedResponse.log = log;
                typedResponse._address = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._from = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse._money = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SmartSocietyAppEventResponse> smartSocietyAppEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SMARTSOCIETYAPP_EVENT));
        return smartSocietyAppEventFlowable(filter);
    }

    @Deprecated
    public static SmartSociety load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SmartSociety(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static SmartSociety load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SmartSociety(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SmartSociety load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new SmartSociety(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SmartSociety load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SmartSociety(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<SmartSociety> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SmartSociety.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<SmartSociety> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SmartSociety.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<SmartSociety> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SmartSociety.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<SmartSociety> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SmartSociety.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class SmartSocietyAppEventResponse {
        public Log log;

        public String _address;

        public String _from;

        public BigInteger _money;
    }
}
