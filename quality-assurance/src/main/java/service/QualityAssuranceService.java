package service;

import ethereum.SmartSociety;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class QualityAssuranceService {

    private final static String PRIVATE_KEY = "56b30630ef0d8bc6f446e8f163025dcc7302a7f2031bc0a94fb4518373911cea";

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);

    private final static String RECEPIENT = "0x9B6AAF2c0F535a2e29A324fa3B420A87448A3a5e";

    private Web3j web3j;

    private String  printWeb3Version(Web3j web3 ){

        Web3ClientVersion web3ClientVersion = null;

        try {
            web3ClientVersion = web3.web3ClientVersion().send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return web3ClientVersion.getWeb3ClientVersion();
    }

    //public void initializeQAInstance(QualityAssurance qualityAssurance,)

    public static void main(String[] args) throws Exception {

        new QualityAssuranceService("");
    }

    public QualityAssuranceService(String ethNode) throws Exception {
        web3j = Web3j.build(new HttpService(ethNode));

        /*TransactionManager transactionManager
                = new RawTransactionManager(web3, getCredentialsFromPrivateKey());
        Transfer transfer = new Transfer(web3, transactionManager);

        TransactionReceipt transactionReceipt = transfer.sendFunds(
                RECEPIENT,
                BigDecimal.ONE,
                Convert.Unit.ETHER,
                GAS_RPICE,
                GAS_LIMIT
        ).send(); */

        ContractGasProvider gasProvider = new ContractGasProvider() {
            @Override
            public BigInteger getGasPrice(String contractFunc) {
                switch (contractFunc) {
                    case SmartSociety.FUNC_CREATESMARTSOCIETYAPPLICATION:
                        return GAS_PRICE;
                    default:
                        return GAS_PRICE;
                }

            }

            @Override
            public BigInteger getGasPrice() {
                return GAS_PRICE;
            }

            @Override
            public BigInteger getGasLimit(String contractFunc) {
                return GAS_LIMIT;
            }

            @Override
            public BigInteger getGasLimit() {
                return GAS_LIMIT;
            }
        };
        SmartSociety smartSociety = SmartSociety.load("0xe0cae096b8fc39e1f9d83ef7d495d7469b006bb7", web3j, getCredentialsFromPrivateKey(), gasProvider);
        //SmartSociety smartSociety = SmartSociety.deploy(web3, getCredentialsFromPrivateKey(), gasProvider).send();
        //System.out.println(smartSociety.getContractAddress());
        List<String> workers = Arrays.asList("0x9B6AAF2c0F535a2e29A324fa3B420A87448A3a5e", "0x1a0c1a29E95d593af2cb5bd87A3a9eb68c6162A4");
        List<String> voters = Arrays.asList("0x5c12018A80186804c8C56AAE17F2DeD1CCd6f90f", "0x27a8f5f731940868eEc4Fea4035EC7Eef884963E", "0x06eCce5290A0923255b55B04EF9e03b7Ef6555FC", "0x6ffAd9BD30cCfc7769f986D2e427ec392102EB0A");
        List<String> substitutes = Arrays.asList("0x731F4fF42Df3FD23DEa16abdA645Cc129e252C61", "0xfa02013F23126EA8c90d40B5fCb9300773fe54DB");
        List<byte[]> tasks = Arrays.asList(Arrays.copyOf("Task 1".getBytes(), 32),Arrays.copyOf("Task 1".getBytes(), 32), Arrays.copyOf("Task 2".getBytes(), 32));
        //134439520000000000000
//, Arrays.copyOf("Task 2".getBytes(), 32), Arrays.copyOf("Task 3".getBytes(), 32), Arrays.copyOf("Task 4".getBytes(), 32)

        //TransactionReceipt address = smartSociety.createSmartSocietyApplication(voters, BigInteger.valueOf(240L), workers, BigInteger.valueOf(280L), substitutes,
               // BigInteger.valueOf(150L), tasks, BigInteger.valueOf(10000000L)).send();
        TransactionReceipt transactionReceipt = smartSociety.createSmartSocietyApplication(voters, BigInteger.valueOf(27L),
                workers, BigInteger.valueOf(37L), substitutes, BigInteger.valueOf(17L), tasks, BigInteger.valueOf(123456)).send();

        EthFilter ethFilter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, smartSociety.getContractAddress());
        Event event = new Event("SmartSocietyApp",
                Arrays.asList(new TypeReference<Address>() {},
                        new TypeReference<Address>() {},
                        new TypeReference<Uint256>() {}));
        String encodedEventSignature = EventEncoder.encode(SmartSociety.SMARTSOCIETYAPP_EVENT);

        ethFilter.addSingleTopic(encodedEventSignature);


        web3j.ethLogFlowable(ethFilter).subscribe(log -> {
            List<Type> args = FunctionReturnDecoder.decode(
                    log.getData(), SmartSociety.SMARTSOCIETYAPP_EVENT.getParameters());
            args.forEach(arg -> System.out.println(arg.getValue()));
        });

        System.out.println("tea");



    }

    private Credentials getCredentialsFromWallet() throws IOException, CipherException {
        return WalletUtils
                .loadCredentials("passphrase",
                        "wallet/path");
    }

    private Credentials getCredentialsFromPrivateKey() {
        return Credentials.create("81068feda8bbd108821b7d48c8ae598bba803d991c0a492fc58c93d9682e3321");
    }

    private static class Builder {

        private QualityAssurance qualityAssurance = new QualityAssurance();
    }


}
