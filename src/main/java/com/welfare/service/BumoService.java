package com.welfare.service;

import com.welfare.entity.BumoEntity;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/9/11 18:01
 * @Description:
 */
public interface BumoService {

    /**
     * 创建账号并激活
     * @return
     */
    public BumoEntity createAccount(long userId);
    public String sendBu(long fromUserId, long toUserId, int amount);
    public String recharge(long userId,int amount);
    public String withdraw(long userId,int amount);

    public void queryAccount(long userId);

    public String queryHash(String hash);
}
