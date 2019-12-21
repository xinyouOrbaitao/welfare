package com.welfare.service.impl;

import com.welfare.dao.UserAccountLogDao;
import com.welfare.dao.WelfareLogDao;
import com.welfare.entity.UserAccountLogEntity;
import com.welfare.entity.WelfareLogEntity;
import com.welfare.service.WelfareLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/8/25 13:33
 * @Description:
 */
@Service
public class WelfareLogServiceImpl implements WelfareLogService {
    @Autowired
    private WelfareLogDao welfareLogDao;
    @Autowired
    private UserAccountLogDao userAccountLogDao;

    @Override
    public List<WelfareLogEntity> selectListByWelfareId(String welfareId) {
        return welfareLogDao.selectListByWelfareId(welfareId);
    }

    @Override
    public List<WelfareLogEntity> selectListByUserId(String userId) {
        return welfareLogDao.selectListByUserId(userId);
    }

    @Override
    public List<UserAccountLogEntity> queryLog(String welfareId) {
        int stat = 2;
        return userAccountLogDao.selectListByWelfareId(stat, welfareId);
    }
}
