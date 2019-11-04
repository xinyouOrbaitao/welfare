package com.welfare.controller;

import com.github.pagehelper.PageInfo;
import com.welfare.entity.WelfareEntity;
import com.welfare.entity.vo.PageParam;
import com.welfare.service.WelfareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private WelfareService welfareService;

    /**
     * 首页
     * @param modelMap
     * @param pageParam
     * @return
     */
    @RequestMapping("/")
    public String index(Model modelMap, PageParam pageParam) {
        PageInfo<WelfareEntity> resultList = welfareService.selectListByIndex(pageParam.getPageNo(), pageParam.getPageSize());

        modelMap.addAttribute("entityList", resultList);
        int totalAmount = welfareService.totalAmount();
        int totalPeople = welfareService.totalPeople();
        //捐款总人数
        modelMap.addAttribute("totalPeople", totalPeople);
        //捐款总金额
        modelMap.addAttribute("totalAmount", totalAmount);
        modelMap.addAttribute("classNum", "1");
        List<WelfareEntity> welfareEntityList = welfareService.selectListToThree();
        //首页轮播图
        modelMap.addAttribute("welfareEntityList", welfareEntityList);
        return "index";
    }
}
