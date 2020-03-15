package com.welfare.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.welfare.TrustMain;
import com.welfare.dao.*;
import com.welfare.entity.*;
import com.welfare.eumn.RelationEnum;
import com.welfare.service.BumoService;
import com.welfare.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/8/25 13:32
 * @Description:
 */
@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    private UserAccountDao userAccountDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserAccountLogDao userAccountLogDao;

    @Autowired
    private WelfareDao welfareDao;

    @Autowired
    private WelfareLogDao welfareLogDao;

    @Autowired
    private BumoService bumoService;
    @Resource
    private TrustMain trustMain;

    /**
     * 充值功能
     * 1.余额增加
     * 2.增加记录
     * 3.调用布比接口
     *
     * @param userId 用户ID
     * @param amount 金额
     */
    @Override
    public JSONObject recharge(long userId, int amount) {
        JSONObject jsonObject = new JSONObject();
        UserAccountEntity userAccountEntity = userAccountDao.selectByUserId(userId);
        if (StringUtils.isEmpty(userAccountEntity)) {
            jsonObject.put("code", "error");
            jsonObject.put("msg", "账号不存在");
        }
        long userMoney = userAccountEntity.getMoney();
        long balance = userMoney + amount;
        userAccountDao.updateUserAccount(balance, userId);
        UserAccountLogEntity userAccountLogEntity = new UserAccountLogEntity();
        userAccountLogEntity.setAmount(amount);
        userAccountLogEntity.setCreateTime(new Date());
        userAccountLogEntity.setType("1");
        userAccountLogEntity.setUserId(userId);
        String hash = bumoService.recharge(userId, amount);
        userAccountLogEntity.setHash(hash);
        userAccountLogDao.insertSelective(userAccountLogEntity);
        jsonObject.put("code", "SUCCESS");
        jsonObject.put("msg", "充值成功");
        return jsonObject;
    }

    /**
     * 提现功能
     * 1.余额减少
     * 2.增加余额记录
     * 3. 减少布比账号余额
     *
     * @param userId
     * @param amount
     */
    @Override
    public JSONObject withdraw(long userId, int amount) {
        JSONObject jsonObject = new JSONObject();
        UserAccountEntity userAccountEntity = userAccountDao.selectByUserId(userId);
        if (StringUtils.isEmpty(userAccountEntity)) {
            jsonObject.put("code", "error");
            jsonObject.put("msg", "账号不存在");
        }
        long userMoney = userAccountEntity.getMoney();
        long balance = userMoney - amount;
        if (balance < 0) {
            jsonObject.put("code", "error");
            jsonObject.put("msg", "账号余额不足");
        } else {
            userAccountDao.updateUserAccount(balance, userId);
            UserAccountLogEntity userAccountLogEntity = new UserAccountLogEntity();
            userAccountLogEntity.setAmount(amount);
            userAccountLogEntity.setCreateTime(new Date());
            userAccountLogEntity.setType("3");
            userAccountLogEntity.setUserId(userId);
            String hash = bumoService.withdraw(userId, amount);
            userAccountLogEntity.setHash(hash);
            userAccountLogDao.insertSelective(userAccountLogEntity);
            jsonObject.put("code", "SUCCESS");
            jsonObject.put("msg", "提现成功");
        }
        return jsonObject;
    }

    @Override
    public PageInfo<UserAccountLogEntity> selectLogList(long userId, int pageNo, int pageSize, String type) {
        PageInfo<UserAccountLogEntity> pageInfo = PageHelper.startPage(pageNo, pageSize).doSelectPageInfo(() -> {
            Integer state = 0;
            if (StringUtils.isEmpty(type)) {
                state = Integer.parseInt(type);
            }
            userAccountLogDao.selectListByState(state, userId);
        });
        return pageInfo;
    }

    @Override
    public List<UserAccountLogEntity> selectLogList(long userId) {
        return userAccountLogDao.selectListByState(0, userId);
    }

    /**
     * 1.先取项目，
     * 2.判断用户账号是否足够，减少余额，
     * 3.判断项目金额是否达到目标，
     * 4.增加项目金额，判断金额是否到目标，是，更改项目状态，
     * 5.调用布比接口
     * 6.增加用户捐款记录，增加项目捐款记录
     *
     * @param userId    用户ID
     * @param welfareId 公益项目ID
     * @param amount    捐赠金额
     */
    @Override
    public JSONObject donate(long userId, String welfareId, int amount, Integer type) {

        JSONObject jsonObject = new JSONObject();
        WelfareEntity welfareEntity = welfareDao.selectWelfareOne(welfareId);
        UserAccountEntity userAccountEntity = userAccountDao.selectByUserId(userId);
        UserAccountEntity toUserEntity = userAccountDao.selectByUserId(Long.parseLong(welfareEntity.getWelfareSponsor()));
        if (StringUtils.isEmpty(userAccountEntity)) {
            jsonObject.put("code", "error");
            jsonObject.put("msg", "账号余额不足");
            return jsonObject;
        }
        if (Long.toString(toUserEntity.getUserId()).equals(Long.toString(userId))) {
            jsonObject.put("code", "error");
            jsonObject.put("msg", "不能給自己捐款！");
            return jsonObject;
        }
        if (StringUtils.isEmpty(welfareEntity)) {
            jsonObject.put("code", "error");
            jsonObject.put("msg", "项目不存在");
            return jsonObject;
        }
        UserEntity param = new UserEntity();
        param.setId(userId);
        UserEntity userEntity = userDao.selectOne(param);
        long userMoney = userAccountEntity.getMoney();
        if (userMoney > amount) {
            long balance = userMoney - amount;
            long toBalance = toUserEntity.getMoney() + amount;
            userAccountDao.updateUserAccount(balance, userId);
            userAccountDao.updateUserAccount(toBalance, toUserEntity.getUserId());
            UserAccountLogEntity userAccountLogEntity = new UserAccountLogEntity();
            userAccountLogEntity.setAmount(amount);
            userAccountLogEntity.setCreateTime(new Date());
            userAccountLogEntity.setWelfareId(welfareId);
            userAccountLogEntity.setWelfareName(welfareEntity.getWelfareName());
            userAccountLogEntity.setType("2");
            userAccountLogEntity.setUsername(userEntity.getUsername());
            userAccountLogEntity.setUserId(userId);
            String hash = bumoService.sendBu(userId, toUserEntity.getUserId(), amount);
            userAccountLogEntity.setHash(hash);
            userAccountLogDao.insertSelective(userAccountLogEntity);
            List<UserAccountLogEntity> list = userAccountLogDao.selectListByUserId(2, welfareId, userId);
            long total = welfareEntity.getWelfareActualAccount() + amount;
            welfareDao.updateWelfareAccount(welfareEntity.getId(), total);
            if (total > welfareEntity.getWelfareAccount()) {
                BigDecimal value = getValue(amount, type, welfareEntity.getWelfareValue(), list.size());
                welfareDao.updateStatus(welfareEntity.getId() + "", "3", value);
            }
            WelfareLogEntity welfareLogEntity = new WelfareLogEntity();
            welfareLogEntity.setCode(welfareEntity.getBuHash());
            welfareLogEntity.setCreateTime(new Date());
            welfareLogEntity.setWelfareId(welfareEntity.getId());
            welfareLogEntity.setWelfareTitle(welfareEntity.getWelfareTitle());

            welfareLogEntity.setWelfareSponsor(userEntity.getId() + "");
            welfareLogEntity.setWelfareSponsorName(userEntity.getUsername());
            welfareLogDao.insertSelective(welfareLogEntity);
            jsonObject.put("code", "SUCCESS");
            jsonObject.put("msg", "捐赠成功");
        }
        return jsonObject;
    }

    @Override
    public UserAccountEntity selectUserAccount(long userId) {
        return userAccountDao.selectByUserId(userId);
    }


    public BigDecimal getValue(Integer amount, Integer type, BigDecimal value, int times) {

        // 初始化项目状态信任值(0.5)
        float state_value = value.floatValue();
        // 1.根据当前用户user_i与项目发起人的关系计算角色信任值;
        // 假设用户user_i与当前项目发起人的关系是“亲戚”;
        RelationEnum relationEnum = RelationEnum.valueToEnum(type);
        float role_value = trustMain.is_relation(relationEnum);

        // 2.根据项目的募捐金额和评估金额计算状态信任值
        float state_change = trustMain.is_disparity(amount);
        state_value -= state_change;

        // 3.计算聚合信用值,即项目的信任值（保留两位小数）
        // 计算捐款金额权重
        float amount_weight = trustMain.is_weight(amount);
        //当前用户向该项目的捐款次数
        float t_trust_value = trustMain.cal_togetherTrust(times, role_value, amount_weight, state_value);
        t_trust_value = (float) (Math.round(t_trust_value * 100.0) / 100.0);
        return new BigDecimal(t_trust_value);
    }

}
