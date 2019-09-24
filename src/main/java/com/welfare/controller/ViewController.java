package com.welfare.controller;

import com.github.pagehelper.PageInfo;
import com.welfare.entity.UserEntity;
import com.welfare.entity.WelfareEntity;
import com.welfare.entity.vo.PageParam;
import com.welfare.service.WelfareService;
import com.welfare.util.LoginAccountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author ：chenxinyou.
 * @Title :
 * @Date ：Created in 2019/8/27 10:28
 * @Description:
 */
@Controller
public class ViewController {
    @Autowired
    private WelfareService welfareService;

    /**
     * 用户
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/user")
    public String user(Model modelMap) {
        return "user";
    }

    /**
     * 充值中心
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/recharge")
    public String recharge(Model modelMap) {
        return "recharge";
    }

    /**
     * 捐款记录
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/donateLog")
    public String donateLog(Model modelMap) {
        return "userWelfareLog";
    }

    /**
     * 项目详情
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/detail")
    public String detail(Model modelMap) {
        return "detail";
    }

    /**
     * 个人中心
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("/alterperson")
    public String alterperson(Model modelMap) {
        return "alterperson";
    }

    @RequestMapping(value = "/listinfo")
    public String selectList(Model modelMap, PageParam pageParam) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity();
        if (StringUtils.isEmpty(userEntity)) {
            return "login";
        }
        PageInfo<WelfareEntity> resultList = welfareService.selectListByIndex(pageParam.getPageNo(), pageParam.getPageSize());
        modelMap.addAttribute("list", resultList);
        return "listinfo";
    }
    @RequestMapping(value = "/addProject")
    public String addProject() {
        return "addproject";
    }

   @RequestMapping(value = "/update/userview")
    public String userview() {
        return "userUpdate";
    }


}
