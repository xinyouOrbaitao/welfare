package com.welfare.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.welfare.entity.UserEntity;
import com.welfare.entity.WelfareEntity;
import com.welfare.entity.WelfareLogEntity;
import com.welfare.entity.vo.PageParam;
import com.welfare.service.WelfareLogService;
import com.welfare.service.WelfareService;
import com.welfare.util.LoginAccountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/8/19 10:29
 * @Description:
 */
@Controller
@RequestMapping("/welfare")
public class WelfareController {
    @Autowired
    private WelfareService welfareService;
    @Autowired
    private WelfareLogService welfareLogService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String add(@RequestBody WelfareEntity entity, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        jsonObject.put("code", "SUCCESS");
        entity.setWelfareSponsor(userEntity.getId() + "");
        entity.setWelfareSponsorName(userEntity.getUsername());
        entity.setWelfareValue(BigDecimal.valueOf(0.5));
        welfareService.save(entity);
        return jsonObject.toJSONString();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String update(WelfareEntity entity) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "SUCCESS");
        welfareService.update(entity);
        return jsonObject.toJSONString();
    }

    @RequestMapping(value = "/selectByUser", method = RequestMethod.POST)
    @ResponseBody
    public String select(PageParam pageParam, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        if (StringUtils.isEmpty(userEntity)) {
            jsonObject.put("code", "error");
            jsonObject.put("msg", "请登录");
        }
        jsonObject.put("code", "SUCCESS");
        PageInfo<WelfareEntity> welfareEntityPageInfo = welfareService.selectListByUser(pageParam.getPageNo(), pageParam.getPageSize(), String.valueOf(userEntity.getId()));
        jsonObject.put("list", welfareEntityPageInfo);
        return jsonObject.toJSONString();
    }

    @RequestMapping(value = "/selectLog", method = RequestMethod.POST)
    @ResponseBody
    public String selectLog(String welfareId, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        if (StringUtils.isEmpty(userEntity)) {
            jsonObject.put("code", "error");
            jsonObject.put("msg", "请登录");
        }
        jsonObject.put("code", "SUCCESS");
        List<WelfareLogEntity> welfareLogList = welfareLogService.selectListByWelfareId(welfareId);
        jsonObject.put("list", welfareLogList);
        return jsonObject.toJSONString();
    }


}
