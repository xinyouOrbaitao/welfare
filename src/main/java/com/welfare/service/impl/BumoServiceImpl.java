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
import io.bumo.model.request.*;
import io.bumo.model.request.operation.*;
import io.bumo.model.response.*;
import io.bumo.model.response.result.AccountGetBalanceResult;
import io.bumo.model.response.result.TransactionBuildBlobResult;
import io.bumo.model.response.result.data.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author ：zhangyue.
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
    public BumoEntity createAccount(long userId) {
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
        return bumoEntity;
    }

    /**
     * 转账
     *
     * @param fromUserId
     * @param toUserId
     * @param amount
     * @return
     */
    @Override
    public String sendBu(long fromUserId, long toUserId, int amount) {
        BumoEntity fromEntity = bumoDao.selectByUserId(fromUserId);
        BumoEntity toEntity = bumoDao.selectByUserId(toUserId);
        if (StringUtils.isEmpty(fromEntity) || StringUtils.isEmpty(toEntity)) {
            return "";
        }
        return sendBu(fromEntity.getPrivateKey(), toEntity.getAddress(), amount);
    }

    @Override
    public void queryAccount(long userId){
        BumoEntity fromEntity = bumoDao.selectByUserId(userId);
        AccountGetBalanceRequest request = new AccountGetBalanceRequest();
        request.setAddress(fromEntity.getAddress());
        // Call getBalance
        AccountGetBalanceResponse response = sdk.getAccountService().getBalance(request);
        if (0 == response.getErrorCode()) {
            AccountGetBalanceResult result = response.getResult();
            logger.info("BU余额：" + ToBaseUnit.MO2BU(result.getBalance().toString()) + " BU");
        } else {
            logger.info("error: " + response.getErrorDesc());
        }
    }

    @Override
    public String queryHash(String hash) {
        String  result = "";
        TransactionGetInfoRequest request = new TransactionGetInfoRequest();
        request.setHash(hash);

        // Call getInfo
        TransactionGetInfoResponse response = sdk.getTransactionService().getInfo(request);
        if (response.getErrorCode() == 0) {
            result = response.getResult().toString();
            System.out.println(JSON.toJSONString(result, true));
        } else {
            System.out.println("error: " + response.getErrorDesc());
        }
        return result;
    }

    public String sendBu(String senderPrivateKey, String destAddress, int buAmount) {
        // Init variable
        // The amount to be sent
        // 发送多少币
        Long amount = ToBaseUnit.BU2MO(String.valueOf(buAmount));
        // The fixed write 1000L, the unit is MO
        // 手续费
        Long gasPrice = 1000L;
        // Set up the maximum cost 0.01BU
        Long feeLimit = ToBaseUnit.BU2MO("0.01");

        // 1. Get the account address to send this transaction
        //获取帐户地址以发送此交易
        String senderAddresss = getAddressByPrivateKey(senderPrivateKey);

        //交易发起账户的现时数+ 1
        // Transaction initiation account's nonce + 1
        Long nonce = getNonce(senderAddresss) + 1;
        // 2. Build sendBU
        //构建发送账号
        BUSendOperation operation = new BUSendOperation();
        operation.setSourceAddress(senderAddresss);
        operation.setDestAddress(destAddress);
        operation.setAmount(amount);

        String[] signerPrivateKeyArr = {senderPrivateKey};
        // Record txhash for subsequent confirmation of the real result of the transaction.
        // After recommending five blocks, call again through txhash `Get the transaction information
        // from the transaction Hash'(see example: getTxByHash ()) to confirm the final result of the transaction
        String txHash = submitTransaction(signerPrivateKeyArr, senderAddresss, operation, nonce, gasPrice, feeLimit);
        if (txHash != null) {
            logger.info("hash: " + txHash);
            return txHash;
        }
        return "";
    }

    /**
     * 充值
     *
     * @param userId
     * @param amount
     * @return
     */
    @Override
    public String recharge(long userId, int amount) {
        BumoEntity userEntity = bumoDao.selectByUserId(userId);
        if (StringUtils.isEmpty(userEntity)) {
            return "";
        }
        return sendBu(creationPrivateKey, userEntity.getAddress(), amount);
    }

    /**
     * 提现
     *
     * @param userId
     * @param amount
     * @return
     */
    @Override
    public String withdraw(long userId, int amount) {
        BumoEntity userEntity = bumoDao.selectByUserId(userId);
        if (StringUtils.isEmpty(userEntity)) {
            return "";
        }
        return sendBu(userEntity.getPrivateKey(), creationAddress, amount);
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
