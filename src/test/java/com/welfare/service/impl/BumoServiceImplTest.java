package com.welfare.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.googlecode.protobuf.format.JsonFormat;
import com.welfare.service.BumoService;
import com.welfare.util.JsonUtils;
import io.bumo.SDK;
import io.bumo.common.ToBaseUnit;
import io.bumo.crypto.Keypair;
import io.bumo.crypto.protobuf.Chain;
import io.bumo.encryption.key.PrivateKey;
import io.bumo.encryption.utils.hex.HexFormat;
import io.bumo.model.request.*;
import io.bumo.model.request.operation.AccountActivateOperation;
import io.bumo.model.request.operation.BaseOperation;
import io.bumo.model.request.operation.ContractCreateOperation;
import io.bumo.model.response.*;
import io.bumo.model.response.result.AccountGetNonceResult;
import io.bumo.model.response.result.TransactionBuildBlobResult;
import io.bumo.model.response.result.data.Signature;
import io.bumo.model.response.result.data.TransactionHistory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author ：chenxinyou.
 * @Title :
 * @Date ：Created in 2019/9/11 18:37
 * @Description:
 */
public class BumoServiceImplTest extends WelfareTest {
    @Autowired
    private BumoService bumoService;

    static String url = "http://10.100.12.129:26002";
    //    static String url = "http://seed1.bumotest.io";
    static SDK sdk = SDK.getInstance(url);

    @Test
    public void createAccount() {
        bumoService.createAccount(0L);
    }


    @Test
    public void test() {

        //        // The account private key to activate a new account
        String activatePrivateKey = "privbwP7UQFVwULm3dJsY5KK7QN5vC6Q9fPEZnQXczAB6B4PFWX2q7xM";
        Long initBalance = ToBaseUnit.BU2MO("1000");
        // The fixed write 1000L, the unit is MO
        Long gasPrice = 1000L;
        // Set up the maximum cost 0.01BU
        Long feeLimit = ToBaseUnit.BU2MO("0.01");
        // Transaction initiation account's nonce + 1
        Long nonce = 8L;


        AccountCreateResponse response = sdk.getAccountService().create();
        if (response.getErrorCode() != 0) {
            System.out.println(JSON.toJSONString(response, true));
        }
        String destAccount = response.getResult().getAddress();

        // 1. Get the account address to send this transaction
        String activateAddresss = getAddressByPrivateKey(activatePrivateKey);
        //buQXD6SoZ4EEborEyd1TgPet72p1U9JdUAMM
        AccountGetNonceRequest request = new AccountGetNonceRequest();
        request.setAddress(activateAddresss);
        // 调用getNonce接口
        AccountGetNonceRequest getNonceRequest = new AccountGetNonceRequest();
        getNonceRequest.setAddress(activateAddresss);

        AccountGetNonceResponse getNonceResponse = sdk.getAccountService().getNonce(getNonceRequest);

// 赋值nonce
        if (getNonceResponse.getErrorCode() == 0) {
            AccountGetNonceResult result = getNonceResponse.getResult();
            System.out.println("nonce: " + result.getNonce());
        } else {
            System.out.println("error" + getNonceResponse.getErrorDesc());
        }

        // 2. Build activateAccount
        AccountActivateOperation operation = new AccountActivateOperation();
        operation.setSourceAddress(activateAddresss);
        operation.setDestAddress(destAccount);
        operation.setInitBalance(initBalance);
        operation.setMetadata("activate account");

        String[] signerPrivateKeyArr = {activatePrivateKey};
        // Record txhash for subsequent confirmation of the real result of the transaction.
        // After recommending five blocks, call again through txhash `Get the transaction information
        // from the transaction Hash'(see example: getTxByHash ()) to confirm the final result of the transaction
        String txHash = submitTransaction(signerPrivateKeyArr, activateAddresss, operation, nonce, gasPrice, feeLimit);
        if (txHash != null) {
            System.out.println("hash: " + txHash);
        }
    }

    private String getAddressByPrivateKey(String privatekey) {
        String publicKey = PrivateKey.getEncPublicKey(privatekey);
        String address = PrivateKey.getEncAddress(publicKey);
        return address;
    }

    /**
     * 提交交易
     *
     * @param senderPrivateKeys The account private keys to sign transaction
     * @param senderAddresss    The account address to start transaction
     * @param operation         operation
     * @param senderNonce       Transaction initiation account's Nonce
     * @param gasPrice          Gas price
     * @param feeLimit          fee limit
     * @return java.lang.String transaction hash
     * @author riven
     */
    private String submitTransaction(String[] senderPrivateKeys, String senderAddresss, BaseOperation operation, Long senderNonce, Long gasPrice, Long feeLimit) {
        // 3. Build transaction
        TransactionBuildBlobRequest transactionBuildBlobRequest = new TransactionBuildBlobRequest();
        transactionBuildBlobRequest.setSourceAddress(senderAddresss);
        transactionBuildBlobRequest.setNonce(senderNonce);
        transactionBuildBlobRequest.setFeeLimit(feeLimit);
        transactionBuildBlobRequest.setGasPrice(gasPrice);
        transactionBuildBlobRequest.addOperation(operation);
        // transactionBuildBlobRequest.setMetadata("abc");

        // 4. Build transaction BLob
        String transactionBlob;
        TransactionBuildBlobResponse transactionBuildBlobResponse = sdk.getTransactionService().buildBlob(transactionBuildBlobRequest);
        if (transactionBuildBlobResponse.getErrorCode() != 0) {
            System.out.println("error: " + transactionBuildBlobResponse.getErrorDesc());
            return null;
        }
        TransactionBuildBlobResult transactionBuildBlobResult = transactionBuildBlobResponse.getResult();
        String txHash = transactionBuildBlobResult.getHash();
        transactionBlob = transactionBuildBlobResult.getTransactionBlob();
        try {
            Chain.Transaction tran = Chain.Transaction.parseFrom(HexFormat.hexToByte(transactionBlob));
            JsonFormat jsonFormat = new JsonFormat();
            System.out.println(jsonFormat.printToString(tran));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        // 5. Sign transaction BLob
        String[] signerPrivateKeyArr = senderPrivateKeys;
        TransactionSignRequest transactionSignRequest = new TransactionSignRequest();
        transactionSignRequest.setBlob(transactionBlob);
        for (int i = 0; i < signerPrivateKeyArr.length; i++) {
            transactionSignRequest.addPrivateKey(signerPrivateKeyArr[i]);
        }
        TransactionSignResponse transactionSignResponse = sdk.getTransactionService().sign(transactionSignRequest);
        if (transactionSignResponse.getErrorCode() != 0) {
            System.out.println("error: " + transactionSignResponse.getErrorDesc());
            return null;
        }

        // 6. Broadcast
        TransactionSubmitRequest transactionSubmitRequest = new TransactionSubmitRequest();
        transactionSubmitRequest.setTransactionBlob(transactionBlob);
        transactionSubmitRequest.setSignatures(transactionSignResponse.getResult().getSignatures());
        TransactionSubmitResponse transactionSubmitResponse = sdk.getTransactionService().submit(transactionSubmitRequest);
        if (0 == transactionSubmitResponse.getErrorCode()) {
            System.out.println("Success，hash=" + transactionSubmitResponse.getResult().getHash());
        } else {
            System.out.println("Failure，hash=" + transactionSubmitResponse.getResult().getHash() + "");
            System.out.println(JSON.toJSONString(transactionSubmitResponse, true));
        }
        return txHash;
    }

    /**
     * buQt9tbohvgrVacLtSuTFe4KDmozHz4JetBX
     * privbyVpBmYrYqmt8mdBFu9heMkjyhNEQqr6mTwzt8gGEPUca6xF71ZY
     * b001f87b261ca5c157236495b557e08dd8c54767f1eb73f72943be3d6a778a9b6ceba89fd1e2
     */
    @Test
    public void test11() {
        String url = "http://10.100.12.129:26002";
//       String url = "http://seed1.bumotest.io:26002";
        sdk = SDK.getInstance(url);
        // Generate a new account to be activated
        //{
        //	"address":"buQfdPz2v4wF9cEAhYL2hmhahKqL7wipxFNu",
        //	"privateKey":"privbvkptwaAv2rbU5LxHCbKuk9KAf7JYWGPwgvV7bmxCU9qwLQUuwNs",
        //	"publicKey":"b001fa2ee57006b1d76fb069293c382a2177fb2e3a39df52d5931bbffcb75dface13c342db2e"
        //}
//        Keypair keypair = Keypair.generator();
        AccountCreateResponse response = sdk.getAccountService().create();
        System.out.println(JSON.toJSONString(response.getResult(), true));
//        String destAccount = keypair.getAddress();
//        String destAccount = "buQfdPz2v4wF9cEAhYL2hmhahKqL7wipxFNu";
        String destAccount = response.getResult().getAddress();
//        String senderPrivateKey = keypair.getPrivateKey();
//        String senderPrivateKey = "privbvkptwaAv2rbU5LxHCbKuk9KAf7JYWGPwgvV7bmxCU9qwLQUuwNs";
        String senderPrivateKey = response.getResult().getPrivateKey();
        long nonce = getAccountNonce(destAccount);
        System.out.println("nonce:" + nonce);
        BaseOperation[] operations = buildOperations(destAccount);
        String senderAddresss = response.getResult().getAddress();
//        String senderAddresss =keypair.getAddress();
        String transactionBlob = seralizeTransaction(senderAddresss, nonce, operations);
        System.out.println("blob: " + transactionBlob);
        Signature[] signatures = signTransaction(senderPrivateKey, transactionBlob);
        System.out.println("signData: " + signatures[0].getSignData());
        System.out.println("publicKey: " + signatures[0].getPublicKey());
        String hash = submitTransaction(transactionBlob, signatures);
        System.out.println("hash: " + hash);
        int status = checkTransactionStatus(hash);
        System.out.println("status: " + status);
    }

    @Test
    public void checkAccountActivated() {
        Keypair keypair = Keypair.generator();
        String url = "http://10.100.12.129:26002";
        sdk = SDK.getInstance(url);
        AccountCheckActivatedRequst request = new AccountCheckActivatedRequst();
        request.setAddress(keypair.getAddress());

        AccountCheckActivatedResponse response = sdk.getAccountService().checkActivated(request);
        if (response.getErrorCode() == 0) {
            System.out.println("account (buQtL9dwfFj4BWGRsMri7GX9nGv4GdjpvAeN) is activated");
        } else {
            System.out.println("error: " + response.getErrorDesc());
        }
    }

    public long getAccountNonce(String accountAddress) {
        long nonce = 0;

        // Init request
//        String accountAddress = "buQt9tbohvgrVacLtSuTFe4KDmozHz4JetBX";
        AccountGetNonceRequest request = new AccountGetNonceRequest();
        request.setAddress(accountAddress);

        // Call getNonce
        AccountGetNonceResponse response = sdk.getAccountService().getNonce(request);
        if (0 == response.getErrorCode()) {
            nonce = response.getResult().getNonce();
        } else {
            System.out.println("error: " + response.getErrorDesc());
        }
        return nonce;
    }


    public int checkTransactionStatus(String txHash) {
        // Init request
        TransactionGetInfoRequest request = new TransactionGetInfoRequest();
        request.setHash(txHash);

        // Call getInfo
        TransactionGetInfoResponse response = sdk.getTransactionService().getInfo(request);
        int errorCode = response.getErrorCode();
        if (errorCode == 0) {
            TransactionHistory transactionHistory = response.getResult().getTransactions()[0];
            errorCode = transactionHistory.getErrorCode();
        }

        return errorCode;
    }

    @Test
    public void test1() {
//        Keypair keypair = Keypair.generator();
//        System.out.println(keypair.getPrivateKey());
//        System.out.println(keypair.getPublicKey());
//        System.out.println(keypair.getAddress());
//        String privateKey = "privbz8miEou55hPQF2vpTkqLCNryM47JWwpdmLC1tzHJjcMNRrMc9W8";
//        String publicKey = "b001e54d7a609442f92d37c164f13b5d173f4f521678a09c8440afadfb72fa57c192f9c0d8bb";
//        String address = keypair.getAddress();
        String address = "buQXD6SoZ4EEborEyd1TgPet72p1U9JdUAMM";
        AccountCheckValidRequest request = new AccountCheckValidRequest();
        request.setAddress(address);

        // 调用checkValid接口
        AccountCheckValidResponse response =
                sdk.getAccountService().checkValid(request);
        if (0 == response.getErrorCode()) {
            System.out.println(response.getResult().isValid());
        } else {
            System.out.println("error: " + response.getErrorDesc());
        }

        String senderAddress = "buQXD6SoZ4EEborEyd1TgPet72p1U9JdUAMM";
        AccountGetNonceRequest getNonceRequest = new AccountGetNonceRequest();
        getNonceRequest.setAddress(senderAddress);
//
//// 调用getNonce接口
        AccountGetNonceResponse getNonceResponse = sdk.getAccountService().getNonce(getNonceRequest);
        AccountCreateResponse accountCreateResponse = sdk.getAccountService().create();
// 赋值nonce
        if (getNonceResponse.getErrorCode() == 0) {
            AccountGetNonceResult result = getNonceResponse.getResult();
            System.out.println("nonce: " + result.getNonce());
        } else {
            System.out.println("error" + getNonceResponse.getErrorDesc());
        }
    }

    /**
     * 检查区块状态
     */
    @Test
    public void checkBlockStatus() {
        BlockCheckStatusResponse response = sdk.getBlockService().checkStatus();
        System.out.println(JsonUtils.convertObjectToString(response));
    }

    /**
     * 获取 区块number
     */
    @Test
    public void getNumber() {
        // 调用getNumber接口
        BlockGetNumberResponse response = sdk.getBlockService().getNumber();
        if (0 == response.getErrorCode()) {
            System.out.println(JSON.toJSONString(response.getResult(), true));
        } else {
            System.out.println("error: " + response.getErrorDesc());
        }
    }

    /**
     * 创建资方账户
     * 在区块上创建用户
     */
    @Test
    public void createAccountOnBumo() {
        //buQorGjGKNMBz6ZkAgVVcZ71zSPvFvbVd6T3
        //privbxw1XWfTtG1tSV7zJTyNdDUY3aX3MXeA33oRCn2DSPpXDBxsbptj
        //b001c5a6377e87fdff84999ea9376d2dcd14f36835f98bbefd0b6d598f4bdd91bdc8138edacd

        //buQt9tbohvgrVacLtSuTFe4KDmozHz4JetBX
        //privbyVpBmYrYqmt8mdBFu9heMkjyhNEQqr6mTwzt8gGEPUca6xF71ZY
        //b001f87b261ca5c157236495b557e08dd8c54767f1eb73f72943be3d6a778a9b6ceba89fd1e2
        AccountCreateResponse accountCreateResponse = sdk.getAccountService().create();
        System.out.println(accountCreateResponse.getResult().getAddress());
        System.out.println(accountCreateResponse.getResult().getPrivateKey());
        System.out.println(accountCreateResponse.getResult().getPublicKey());
    }

    /**
     * 获取资方账户的序列号
     * 每个账户都维护着自己的序列号，该序列号从1开始，依次递增，一个序列号标志着一个该账户的交易
     */
    @Test
    public void getNonce() {
//        String accountAddress = "buQorGjGKNMBz6ZkAgVVcZ71zSPvFvbVd6T3";
        String accountAddress = "buQmrPwAM3FeonTVENrRLT6xef259DSzGggE";
        long nonce = 0;
        AccountGetNonceRequest request = new AccountGetNonceRequest();
        request.setAddress(accountAddress);

        // Call getNonce
        AccountGetNonceResponse response = sdk.getAccountService().getNonce(request);
        if (0 == response.getErrorCode()) {
            nonce = response.getResult().getNonce();
        } else {
            System.out.println("error: " + response.getErrorDesc());
        }
    }

    /**
     * 1.
     * 发送交易
     *
     * @param transactionBlob 序列化交易生成
     * @param signatures
     * @return
     */
    public String submitTransaction(String transactionBlob, Signature[] signatures) {
        String hash = null;

        // Submit transaction
        TransactionSubmitRequest transactionSubmitRequest = new TransactionSubmitRequest();
        transactionSubmitRequest.setTransactionBlob(transactionBlob);
        transactionSubmitRequest.setSignatures(signatures);
        TransactionSubmitResponse transactionSubmitResponse = sdk.getTransactionService().submit(transactionSubmitRequest);
        if (0 == transactionSubmitResponse.getErrorCode()) {
            hash = transactionSubmitResponse.getResult().getHash();
        } else {
            System.out.println("error: " + transactionSubmitResponse.getErrorDesc());
        }
        return hash;
    }

    /**
     * 2.
     * 签名交易
     *
     * @param transactionBlob
     * @return
     */
    public Signature[] signTransaction(String senderPrivateKey, String transactionBlob) {
        Signature[] signatures = null;
        // The account private key to issue atp1.0 token
//        String senderPrivateKey = "privbyVpBmYrYqmt8mdBFu9heMkjyhNEQqr6mTwzt8gGEPUca6xF71ZY";

        // Sign transaction BLob
        TransactionSignRequest transactionSignRequest = new TransactionSignRequest();
        transactionSignRequest.setBlob(transactionBlob);
        transactionSignRequest.addPrivateKey(senderPrivateKey);
        TransactionSignResponse transactionSignResponse = sdk.getTransactionService().sign(transactionSignRequest);
        if (transactionSignResponse.getErrorCode() == 0) {
            signatures = transactionSignResponse.getResult().getSignatures();
        } else {
            System.out.println("error: " + transactionSignResponse.getErrorDesc());
        }
        return signatures;
    }

    /**
     * 组装发行资产操作
     *
     * @return
     */
    public BaseOperation[] buildOperations(String createContractAddress) {
        // The account address to issue apt1.0 token
//        String createContractAddress = "buQt9tbohvgrVacLtSuTFe4KDmozHz4JetBX";
        // Contract account initialization BU，the unit is MO，and 1 BU = 10^8 MO
        Long initBalance = ToBaseUnit.BU2MO("0.01");
        // The token name
        String name = "Contract Global";
        // The token code
        String symbol = "CGO";
        // The token total supply, which includes the dicimals.
        // If decimals is 8 and you want to issue 10 tokens now, the nowSupply must be 10 * 10 ^ 8, like below.
        String totalSupply = "1000000000";
        // The token decimals
        Integer decimals = 8;
        // Contract code
        String payload = "'use strict';let globalAttribute={};const globalAttributeKey='global_attribute';function makeAllowanceKey(owner,spender){return'allow_'+owner+'_to_'+spender;}function approve(spender,value){assert(addressCheck(spender)===true,'Arg-spender is not a valid address.');assert(stoI64Check(value)===true,'Arg-value must be alphanumeric.');assert(int64Compare(value,'0')>0,'Arg-value of spender '+spender+' must be greater than 0.');let key=makeAllowanceKey(sender,spender);storageStore(key,value);tlog('approve',sender,spender,value);return true;}function allowance(owner,spender){assert(addressCheck(owner)===true,'Arg-owner is not a valid address.');assert(addressCheck(spender)===true,'Arg-spender is not a valid address.');let key=makeAllowanceKey(owner,spender);let value=storageLoad(key);assert(value!==false,'Failed to get the allowance given to '+spender+' by '+owner+' from metadata.');return value;}function transfer(to,value){assert(addressCheck(to)===true,'Arg-to is not a valid address.');assert(stoI64Check(value)===true,'Arg-value must be alphanumeric.');assert(int64Compare(value,'0')>0,'Arg-value must be greater than 0.');if(sender===to){tlog('transfer',sender,to,value);return true;}let senderValue=storageLoad(sender);assert(senderValue!==false,'Failed to get the balance of '+sender+' from metadata.');assert(int64Compare(senderValue,value)>=0,'Balance:'+senderValue+' of sender:'+sender+' < transfer value:'+value+'.');let toValue=storageLoad(to);toValue=(toValue===false)?value:int64Add(toValue,value);storageStore(to,toValue);senderValue=int64Sub(senderValue,value);storageStore(sender,senderValue);tlog('transfer',sender,to,value);return true;}function transferFrom(from,to,value){assert(addressCheck(from)===true,'Arg-from is not a valid address.');assert(addressCheck(to)===true,'Arg-to is not a valid address.');assert(stoI64Check(value)===true,'Arg-value must be alphanumeric.');assert(int64Compare(value,'0')>0,'Arg-value must be greater than 0.');if(from===to){tlog('transferFrom',sender,from,to,value);return true;}let fromValue=storageLoad(from);assert(fromValue!==false,'Failed to get the value, probably because '+from+' has no value.');assert(int64Compare(fromValue,value)>=0,from+' Balance:'+fromValue+' < transfer value:'+value+'.');let allowValue=allowance(from,sender);assert(int64Compare(allowValue,value)>=0,'Allowance value:'+allowValue+' < transfer value:'+value+' from '+from+' to '+to+'.');let toValue=storageLoad(to);toValue=(toValue===false)?value:int64Add(toValue,value);storageStore(to,toValue);fromValue=int64Sub(fromValue,value);storageStore(from,fromValue);let allowKey=makeAllowanceKey(from,sender);allowValue=int64Sub(allowValue,value);storageStore(allowKey,allowValue);tlog('transferFrom',sender,from,to,value);return true;}function balanceOf(address){assert(addressCheck(address)===true,'Arg-address is not a valid address.');let value=storageLoad(address);assert(value!==false,'Failed to get the balance of '+address+' from metadata.');return value;}function init(input_str){let params=JSON.parse(input_str).params;assert(stoI64Check(params.totalSupply)===true&&params.totalSupply>0&&typeof params.name==='string'&&params.name.length>0&&typeof params.symbol==='string'&&params.symbol.length>0&&typeof params.decimals==='number'&&params.decimals>=0,'Failed to check args');globalAttribute.totalSupply=params.totalSupply;globalAttribute.name=params.name;globalAttribute.symbol=params.symbol;globalAttribute.version='ATP20';globalAttribute.decimals=params.decimals;storageStore(globalAttributeKey,JSON.stringify(globalAttribute));storageStore(sender,globalAttribute.totalSupply);}function main(input_str){let input=JSON.parse(input_str);if(input.method==='transfer'){transfer(input.params.to,input.params.value);}else if(input.method==='transferFrom'){transferFrom(input.params.from,input.params.to,input.params.value);}else if(input.method==='approve'){approve(input.params.spender,input.params.value);}else{throw'<Main interface passes an invalid operation type>';}}function query(input_str){let result={};let input=JSON.parse(input_str);if(input.method==='tokenInfo'){globalAttribute=JSON.parse(storageLoad(globalAttributeKey));result.tokenInfo=globalAttribute;}else if(input.method==='allowance'){result.allowance=allowance(input.params.owner,input.params.spender);}else if(input.method==='balanceOf'){result.balance=balanceOf(input.params.address);}else{throw'<Query interface passes an invalid operation type>';}return JSON.stringify(result);}";

        // Init initInput
        JSONObject initInput = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("name", name);
        params.put("symbol", symbol);
        params.put("decimals", decimals);
        params.put("totalSupply", totalSupply);
        initInput.put("params", params);

        // Build create contract operation
        ContractCreateOperation contractCreateOperation = new ContractCreateOperation();
        contractCreateOperation.setSourceAddress(createContractAddress);
        contractCreateOperation.setInitBalance(initBalance);
        contractCreateOperation.setPayload(payload);
        contractCreateOperation.setInitInput(initInput.toJSONString());
        contractCreateOperation.setMetadata("create ctp 1.0 contract");

        BaseOperation[] operations = {contractCreateOperation};
        return operations;
    }

    /**
     * 3.
     * 序列化交易
     *
     * @param nonce
     * @param operations
     * @return
     */
    public String seralizeTransaction(String senderAddresss, Long nonce, BaseOperation[] operations) {
        String transactionBlob = null;

        // The account address to create contract and issue ctp 1.0 token
//        String senderAddresss = "buQt9tbohvgrVacLtSuTFe4KDmozHz4JetBX";
        // The gasPrice is fixed at 1000L, the unit is MO
        Long gasPrice = 1000L;
        // Set up the maximum cost 10.08BU
        Long feeLimit = ToBaseUnit.BU2MO("10.08");
        // Nonce should add 1
        nonce += 1;

        // Build transaction  Blob
        TransactionBuildBlobRequest transactionBuildBlobRequest = new TransactionBuildBlobRequest();
        transactionBuildBlobRequest.setSourceAddress(senderAddresss);
        transactionBuildBlobRequest.setNonce(nonce);
        transactionBuildBlobRequest.setFeeLimit(feeLimit);
        transactionBuildBlobRequest.setGasPrice(gasPrice);
        for (int i = 0; i < operations.length; i++) {
            transactionBuildBlobRequest.addOperation(operations[i]);
        }
        TransactionBuildBlobResponse transactionBuildBlobResponse = sdk.getTransactionService().buildBlob(transactionBuildBlobRequest);
        if (transactionBuildBlobResponse.getErrorCode() == 0) {
            transactionBlob = transactionBuildBlobResponse.getResult().getTransactionBlob();
        } else {
            System.out.println("error: " + transactionBuildBlobResponse.getErrorDesc());
        }
        return transactionBlob;
    }
}