package com.welfare.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.googlecode.protobuf.format.JsonFormat;
import com.welfare.dao.BumoDao;
import com.welfare.entity.BumoEntity;
import com.welfare.service.BumoService;
import io.bumo.SDK;
import io.bumo.common.ToBaseUnit;
import io.bumo.crypto.Keypair;
import io.bumo.crypto.protobuf.Chain;
import io.bumo.encryption.key.PrivateKey;
import io.bumo.encryption.utils.hex.HexFormat;
import io.bumo.model.request.AccountGetNonceRequest;
import io.bumo.model.request.TransactionBuildBlobRequest;
import io.bumo.model.request.TransactionSignRequest;
import io.bumo.model.request.TransactionSubmitRequest;
import io.bumo.model.request.operation.AccountActivateOperation;
import io.bumo.model.request.operation.AccountSetMetadataOperation;
import io.bumo.model.request.operation.AssetIssueOperation;
import io.bumo.model.request.operation.BaseOperation;
import io.bumo.model.response.AccountGetNonceResponse;
import io.bumo.model.response.TransactionBuildBlobResponse;
import io.bumo.model.response.TransactionSignResponse;
import io.bumo.model.response.TransactionSubmitResponse;
import io.bumo.model.response.result.TransactionBuildBlobResult;
import io.bumo.model.response.result.data.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Author ：chenxinyou.
 * @Title :
 * @Date ：Created in 2019/9/11 18:12
 * @Description:
 */
@Service
public class BumoServiceImpl implements BumoService {
    private Logger logger = LoggerFactory.getLogger(BumoServiceImpl.class);
    @Autowired
    private SDK sdk;

    @Value("${welfare.bumo.privateKey}")
    private String creationPrivateKey;

    @Value("${welfare.bumo.address}")
    private String creationAddress;

    @Autowired
    private BumoDao bumoDao;

    /**
     * 创建新的用户，并激活
     */
    @Override
    public void createAccount(long userId) {
        BumoEntity bumoEntity = new BumoEntity();

        Long initBalance = ToBaseUnit.BU2MO("1000");
        // 固定写入1000L，单位为MO
        Long gasPrice = 1000L;
        // 设置最大成本0.01BU
        Long feeLimit = ToBaseUnit.BU2MO("0.01");
        // 交易启动帐户的nonce + 1
        Long nonce = getNonce(creationAddress) + 1;

        // 生成要激活的新帐户
        Keypair keypair = Keypair.generator();
        logger.info(JSON.toJSONString(keypair, true));
        String destAccount = keypair.getAddress();
        bumoEntity.setAddress(destAccount);
        bumoEntity.setPrivateKey(keypair.getPrivateKey());
        bumoEntity.setPublicKey(keypair.getPublicKey());
        bumoEntity.setUserId(userId);
        // 1. 获取帐户地址以发送此交易
        String activateAddresss = getAddressByPrivateKey(creationPrivateKey);

        // 2.构建激活帐户
        AccountActivateOperation operation = new AccountActivateOperation();
        operation.setSourceAddress(activateAddresss);
        operation.setDestAddress(destAccount);
        operation.setInitBalance(initBalance);
        operation.setMetadata("activate account");

        String[] signerPrivateKeyArr = {creationPrivateKey};
        // 记录txhash以便随后确认交易的实际结果。
        //  推荐五个区块后，再次通过txhash“获取交易信息”
        //  来自事务Hash'（参见示例：getTxByHash（））来确认事务的最终结果
        String txHash = submitTransaction(signerPrivateKeyArr, activateAddresss, operation, nonce, gasPrice, feeLimit);
        if (txHash != null) {
            logger.info("hash: " + txHash);
            bumoEntity.setTxHash(txHash);
            bumoDao.insertSelective(bumoEntity);
        }
    }

    /**
     * 组装发行资产操作
     * @return
     */
    public BaseOperation[] buildOperations() {
        // The account address to issue apt1.0 token
        String issuerAddress = "";
        // The token name
        String name = "Global";
        // The token code
        String code = "GLA";
        // The apt token version
        String version = "1.0";
        // The apt token icon
        String icon = "";
        // The token total supply number
        Long totalSupply = 1000000000L;
        // The token now supply number
        Long nowSupply = 1000000000L;
        // The token description
        String description = "GLA TOKEN";
        // The token decimals
        Integer decimals = 0;

        // Build asset issuance operation
        AssetIssueOperation assetIssueOperation = new AssetIssueOperation();
        assetIssueOperation.setSourceAddress(issuerAddress);
        assetIssueOperation.setCode(code);
        assetIssueOperation.setAmount(nowSupply);

        // If this is an atp 1.0 token, you must set metadata like this
        JSONObject atp10Json = new JSONObject();
        atp10Json.put("name", name);
        atp10Json.put("code", code);
        atp10Json.put("description", description);
        atp10Json.put("decimals", decimals);
        atp10Json.put("totalSupply", totalSupply);
        atp10Json.put("icon", icon);
        atp10Json.put("version", version);

        String key = "asset_property_" + code;
        String value = atp10Json.toJSONString();
        // Build setMetadata
        AccountSetMetadataOperation accountSetMetadataOperation = new AccountSetMetadataOperation();
        accountSetMetadataOperation.setSourceAddress(issuerAddress);
        accountSetMetadataOperation.setKey(key);
        accountSetMetadataOperation.setValue(value);

        BaseOperation[] operations = {assetIssueOperation, accountSetMetadataOperation};
        return operations;
    }

    public String seralizeTransaction(Long nonce,  BaseOperation[] operations) {
        String transactionBlob = null;

        // The account address to issue atp1.0 token
        String senderAddresss = "";
        // The gasPrice is fixed at 1000L, the unit is MO
        Long gasPrice = 1000L;
        // Set up the maximum cost 50.03BU
        Long feeLimit = ToBaseUnit.BU2MO("50.03");
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
            transactionBlob = transactionBuildBlobResponse. getResult().getTransactionBlob();
        } else {
            System.out.println("error: " + transactionBuildBlobResponse.getErrorDesc());
        }
        return transactionBlob;
    }

    public Signature[] signTransaction(String transactionBlob) {
        Signature[] signatures = null;
        // The account private key to issue atp1.0 token
        String senderPrivateKey = "";

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

    public String submitTransaction(String transactionBlob, Signature[] signatures) {
        String  hash = null;
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
        return  hash ;
    }
    /**
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
            logger.info("error: " + transactionBuildBlobResponse.getErrorDesc());
            return null;
        }
        TransactionBuildBlobResult transactionBuildBlobResult = transactionBuildBlobResponse.getResult();
        String txHash = transactionBuildBlobResult.getHash();
        transactionBlob = transactionBuildBlobResult.getTransactionBlob();
        try {
            Chain.Transaction tran = Chain.Transaction.parseFrom(HexFormat.hexToByte(transactionBlob));
            JsonFormat jsonFormat = new JsonFormat();
            logger.info(jsonFormat.printToString(tran));
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
            logger.info("error: " + transactionSignResponse.getErrorDesc());
            return null;
        }

        // 6. Broadcast
        TransactionSubmitRequest transactionSubmitRequest = new TransactionSubmitRequest();
        transactionSubmitRequest.setTransactionBlob(transactionBlob);
        transactionSubmitRequest.setSignatures(transactionSignResponse.getResult().getSignatures());
        TransactionSubmitResponse transactionSubmitResponse = sdk.getTransactionService().submit(transactionSubmitRequest);
        if (0 == transactionSubmitResponse.getErrorCode()) {
            logger.info("Success，hash=" + transactionSubmitResponse.getResult().getHash());
            return txHash;
        } else {
            logger.info("Failure，hash=" + transactionSubmitResponse.getResult().getHash() + "");
            logger.info(JSON.toJSONString(transactionSubmitResponse, true));
            return txHash;
        }
    }

    public long getNonce(String accountAddress) {
        // Init request
        AccountGetNonceRequest request = new AccountGetNonceRequest();
        request.setAddress(accountAddress);
        // Call getNonce
        AccountGetNonceResponse response = sdk.getAccountService().getNonce(request);
        if (0 == response.getErrorCode()) {
            long nonce = response.getResult().getNonce();
            logger.info("账户nonce:" + nonce);
            return nonce;
        } else {
            logger.info("error: " + response.getErrorDesc());
            return 0L;
        }
    }

    private String getAddressByPrivateKey(String privatekey) {
        String publicKey = PrivateKey.getEncPublicKey(privatekey);
        String address = PrivateKey.getEncAddress(publicKey);
        return address;
    }
}
