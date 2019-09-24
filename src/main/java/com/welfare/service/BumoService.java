package com.welfare.service;

import io.bumo.model.response.result.AccountCreateResult;

/**
 * @Author ：chenxinyou.
 * @Title :
 * @Date ：Created in 2019/9/11 18:01
 * @Description:
 */
public interface BumoService {

    /**
     * 创建账号并激活
     * @return
     */
    public void createAccount(long userId);

}
