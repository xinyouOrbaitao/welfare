package com.welfare.service.impl;

import com.welfare.dao.UserAccountDao;
import com.welfare.dao.UserDao;
import com.welfare.entity.BumoEntity;
import com.welfare.entity.UserAccountEntity;
import com.welfare.entity.UserEntity;
import com.welfare.service.BumoService;
import com.welfare.service.UserService;
import com.welfare.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/7/10 15:01
 * @Description:
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAccountDao userAccountDao;
    @Autowired
    private BumoService bumoService;
    @Override
    public String register(String username, String password, String phone) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(phone)) {
            return "";
        }
        String md5 = MD5Util.getMD5(password);
        UserEntity entity = new UserEntity();
        entity.setUsername(username);
        entity.setPassword(md5);
        entity.setPhone(phone);
        int result = userDao.insertSelective(entity);

        //曾经没有注释下面这段代码
        entity = userDao.queryOne(username);

        //调用布比接口，生成用户账号
        BumoEntity bumoEntity = bumoService.createAccount(entity.getId());
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.setUserId(entity.getId());

        userAccountEntity.setMoney(0);
        userAccountEntity.setCode(bumoEntity.getTxHash());
        userAccountDao.insert(userAccountEntity);
        return "SUCCESS";
    }

    @Override
    public UserEntity login(String username, String password) {

        UserEntity entity = userDao.queryOne(username);
        if (StringUtils.isEmpty(entity)) {
            return null;
        }
        return entity;
    }

    @Override
    public UserEntity queryOne(String username) {
        UserEntity param = new UserEntity();
        param.setUsername(username);
        UserEntity entity = userDao.selectOne(param);
        return entity;
    }

    @Override
    public UserEntity queryOneByUserId(Integer userId) {
        UserEntity param = new UserEntity();
        param.setId(userId);
        UserEntity entity = userDao.selectOne(param);
        return entity;
    }

    @Override
    public String updatePassword(String username, String password, String phone) {
        String md5 = MD5Util.getMD5(password);
       int count = userDao.updatePassword(username, md5,phone);
        if (count == 1) {
            return "SUCCESS";
        }
        return "error";
    }

    @Override
    public void update(UserEntity userEntity) {
        userDao.updateByPrimaryKeySelective(userEntity);
    }

}
