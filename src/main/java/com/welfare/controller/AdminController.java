package com.welfare.controller;

import com.alibaba.fastjson.JSONObject;
import com.welfare.entity.WelfareEntity;
import com.welfare.service.AdminService;
import com.welfare.service.WelfareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author ：chenxinyou.
 * @Title :
 * @Date ：Created in 2019/10/14 14:34
 * @Description:
 */
@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @RequestMapping(value = "/review")
    @ResponseBody
    public String review(String welfareId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "SUCCESS");
        adminService.updateWelfareStatus(welfareId,"2");
        return jsonObject.toJSONString();
    }
}
