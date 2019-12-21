package com.welfare.service;

import com.welfare.entity.UserAccountLogEntity;
import com.welfare.entity.WelfareLogEntity;

import java.util.List;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/7/10 15:01
 * @Description:
 */
public interface WelfareLogService {

    /**
     * 查询项目捐赠日志
     * @param welfareId
     * @return
     */
    public List<WelfareLogEntity> selectListByWelfareId(String welfareId);

    /**
     * 根据用户id查询用户捐赠记录
     * @param userId
     * @return
     */
    public List<WelfareLogEntity> selectListByUserId(String userId);

    public List<UserAccountLogEntity> queryLog(String welfareId);

}
