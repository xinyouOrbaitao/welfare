package com.welfare.service.impl;

import com.welfare.entity.BumoEntity;
import com.welfare.service.BumoService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/10/14 17:03
 * @Description:
 */
public class BumoServiceImplTest extends WelfareTest {
    @Autowired
    private BumoService bumoService;

    @Test
    public void createAccount() {
        BumoEntity bumoEntity = bumoService.createAccount(11);

    }

    @Test
    public void queryAccount() {
        bumoService.queryAccount(10);
    }@Test
    public void recharge() {
        bumoService.recharge(11,1000);
    }
}