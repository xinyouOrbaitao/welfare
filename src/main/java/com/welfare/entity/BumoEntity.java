package com.welfare.entity;

import javax.persistence.Table;

/**
 * @Author ：chenxinyou.
 * @Title :
 * @Date ：Created in 2019/9/19 15:10
 * @Description:
 */
@Table(name = "bumo")
public class BumoEntity {

    private long id;
    private long userId;
    private String address;
    private String privateKey;
    private String publicKey;
    private String txHash;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }
}
