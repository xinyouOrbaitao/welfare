package com.welfare.controller;

import com.github.pagehelper.PageInfo;
import com.welfare.entity.*;
import com.welfare.entity.vo.PageParam;
import com.welfare.entity.vo.ParamVO;
import com.welfare.service.UserAccountService;
import com.welfare.service.UserService;
import com.welfare.service.WelfareLogService;
import com.welfare.service.WelfareService;
import com.welfare.util.LoginAccountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/8/27 10:28
 * @Description:
 */
@Controller
public class ViewController {
    @Autowired
    private WelfareService welfareService;

    @Autowired
    private UserService userService;
    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private WelfareLogService welfareLogService;

    /**
     * 用户
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/user")
    public String user(Model modelMap, HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        modelMap.addAttribute("user", userEntity);
        List<WelfareEntity> welfareEntityPageInfo = welfareService.selectListByUser(String.valueOf(userEntity.getId()));
        modelMap.addAttribute("list", welfareEntityPageInfo);
        modelMap.addAttribute("size", welfareEntityPageInfo.size());
        modelMap.addAttribute("classNum", "3");
        return "user";
    }

    /**
     * 充值中心
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/recharge")
    public String recharge(Model modelMap, HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        modelMap.addAttribute("user", userEntity);
        UserAccountEntity userAccountEntity = userAccountService.selectUserAccount(userEntity.getId());
        if (userAccountEntity == null) {
            userAccountEntity = new UserAccountEntity();
        }
        modelMap.addAttribute("userAccountEntity", userAccountEntity);
        return "recharge";
    }

    /**
     * 提现
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/withdraw")
    public String withdraw(Model modelMap, HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        modelMap.addAttribute("user", userEntity);
        UserAccountEntity userAccountEntity = userAccountService.selectUserAccount(userEntity.getId());
        if (userAccountEntity == null) {
            userAccountEntity = new UserAccountEntity();
        }
        modelMap.addAttribute("userAccountEntity", userAccountEntity);
        return "withdraw";
    }

    /**
     * 捐款记录
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/donateLog")
    public String donateLog(Model modelMap, HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        modelMap.addAttribute("user", userEntity);
        List<WelfareLogEntity> welfareLogEntityList = welfareLogService.selectListByUserId(userEntity.getId() + "");
        List<Long> paramList = welfareLogEntityList.stream().map(entity -> entity.getWelfareId()).collect(Collectors.toList());

        List<WelfareEntity> resultList = welfareService.selectListByIdList(paramList);
        resultList.stream().forEach(entity -> {
            int welfarePeopleSize = welfareService.welfarePeopleSize(entity.getId() + "");
            entity.setPeopleSize(welfarePeopleSize + "");
        });
        modelMap.addAttribute("list", resultList);
        return "userWelfareLog";
    }

    /**
     * @param modelMap
     * @return
     */
    @RequestMapping("/donation/{id}")
    public String donation(Model modelMap, @PathVariable String id, HttpServletRequest request) {
        WelfareEntity welfareEntity = welfareService.selectById(id);
        modelMap.addAttribute("entity", welfareEntity);
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        modelMap.addAttribute("user", userEntity);
        return "donation";
    }

    /**
     * @param modelMap
     * @return
     */
    @RequestMapping("/review/{id}")
    public String review(Model modelMap, @PathVariable String id, HttpServletRequest request) {
        WelfareEntity welfareEntity = welfareService.selectById(id);
        modelMap.addAttribute("entity", welfareEntity);
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        modelMap.addAttribute("user", userEntity);
        return "review";
    }

    /**
     * 项目详情
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/welfare/detail/{id}")
    public String detail(Model modelMap, @PathVariable String id, HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        modelMap.addAttribute("user", userEntity);
        WelfareEntity welfareEntity = welfareService.selectById(id);
        modelMap.addAttribute("entity", welfareEntity);
        String welfareId = welfareEntity.getId() + "";
        int welfarePeopleSize = welfareService.welfarePeopleSize(welfareId);
        modelMap.addAttribute("peopleSize", welfarePeopleSize);
        List<UserAccountLogEntity> welfareLogEntityList = welfareLogService.queryLog(welfareId);
        modelMap.addAttribute("welfareLogEntityList", welfareLogEntityList);

        UserEntity paramUserName = userService.queryOneByUserId(Integer.parseInt(welfareEntity.getWelfareSponsor()));
        modelMap.addAttribute("username", paramUserName.getUsername());
        return "detail";
    }

    /**
     * 我的项目详情
     *
     * @param modelMap
     * @param id
     * @return
     */
    @RequestMapping("/welfare/mydetail/{id}")
    public String mydetail(Model modelMap, @PathVariable String id, HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        modelMap.addAttribute("user", userEntity);
        WelfareEntity welfareEntity = welfareService.selectById(id);
        modelMap.addAttribute("entity", welfareEntity);
        String welfareId = welfareEntity.getId() + "";
        int welfarePeopleSize = welfareService.welfarePeopleSize(welfareId);
        modelMap.addAttribute("peopleSize", welfarePeopleSize);
        List<UserAccountLogEntity> welfareLogEntityList = welfareLogService.queryLog(welfareId);
        modelMap.addAttribute("welfareLogEntityList", welfareLogEntityList);
        return "mydetail";
    }

    /**
     * 账户中心
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/account")
    public String account(Model modelMap, HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        List<UserAccountLogEntity> list = userAccountService.selectLogList(userEntity.getId());
        UserAccountEntity userAccountEntity = userAccountService.selectUserAccount(userEntity.getId());
        if (userAccountEntity == null) {
            userAccountEntity = new UserAccountEntity();
        }
        modelMap.addAttribute("user", userEntity);
        modelMap.addAttribute("list", list);
        modelMap.addAttribute("userAccountEntity", userAccountEntity);
        return "account";
    }

    /**
     * 项目列表
     *
     * @param modelMap
     * @param pageParam
     * @return
     */
    @RequestMapping(value = "/listinfo")
    public String selectList(Model modelMap, PageParam pageParam, HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        if (userEntity != null) {
            modelMap.addAttribute("user", userEntity);
        }

        PageInfo<WelfareEntity> resultList = welfareService.selectListByIndex(pageParam.getPageNo(), pageParam.getPageSize());
        resultList.getList().stream().forEach(entity -> {
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            String result = numberFormat.format(entity.getWelfareValue().multiply(new BigDecimal(100)).intValue()) + "%";
            entity.setRatio(result);
            String style = "width:" + result;
            entity.setStyle(style);
            int welfarePeopleSize = welfareService.welfarePeopleSize(entity.getId() + "");
            entity.setPeopleSize(welfarePeopleSize + "");
        });
        modelMap.addAttribute("list", resultList);
        modelMap.addAttribute("classNum", "2");
        return "listinfo";
    }

    /**
     * 管理员项目列表
     *
     * @param modelMap
     * @param paramVO
     * @return
     */
    @RequestMapping(value = "/adminlistinfo")
    public String selectListAdmin(Model modelMap, ParamVO paramVO, HttpServletRequest request) {

        PageInfo<WelfareEntity> resultList = welfareService.selectListByAdmin(paramVO.getPageNo(), paramVO.getPageSize(), paramVO.getType());
        resultList.getList().stream().forEach(entity -> {
            long num1 = entity.getWelfareActualAccount();
            long num2 = entity.getWelfareAccount();
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            String result = numberFormat.format((float) num1 / (float) num2 * 100) + "%";
            entity.setRatio(result);
            String style = "width:" + result;
            entity.setStyle(style);
            int welfarePeopleSize = welfareService.welfarePeopleSize(entity.getId() + "");
            entity.setPeopleSize(welfarePeopleSize + "");
        });
        modelMap.addAttribute("list", resultList);
        return "adminlistinfo";
    }

    /**
     * 创建项目
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/addProject")
    public String addProject(Model modelMap, HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        modelMap.addAttribute("user", userEntity);
        modelMap.addAttribute("classNum", "4");
        return "addproject";
    }

    @RequestMapping(value = "/update/userview")
    public String userview(Model modelMap, HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        modelMap.addAttribute("user", userEntity);
        return "userUpdate";
    }

    @RequestMapping(value = "/query/hash")
    public String queryHash(Model modelMap, HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        modelMap.addAttribute("user", userEntity);
        return "hash";
    }
}
