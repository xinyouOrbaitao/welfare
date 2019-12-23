package com.welfare.controller;

import com.welfare.entity.UserEntity;
import com.welfare.service.UserService;
import com.welfare.util.CookieUtil;
import com.welfare.util.LoginAccountUtil;
import com.welfare.util.MD5Util;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.OutputStream;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/7/11 14:14
 * @Description:
 */
@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    /* 登录界面*/
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {

        return new ModelAndView("login");
    }

   /* 注册界面*/
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register() {

        return new ModelAndView("register");
    }
    @RequestMapping("/getCode")
    public void getCode(HttpServletResponse response, HttpServletRequest request) {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", -1);
        response.setContentType("image/jpeg");

        String code  = LoginAccountUtil.getCode();
        if (!StringUtils.isEmpty(code)) {
            CookieUtil.addCode(response,code,request);
            OutputStream sos = null;
            try {
                RenderedImage image = LoginAccountUtil.generateCodeAndPic(code);
                sos = response.getOutputStream();
                ImageIO.write(image, "jpeg", sos);
                sos.close();
                response.flushBuffer();
            } catch (Exception e) {

            } finally {
                try {
                    if (sos != null) {
                        sos.close();
                    }
                } catch (Exception e) {

                }
            }
        }
    }
    /* 忘记密码界面*/
    @RequestMapping(value = "/updatePassword", method = RequestMethod.GET)
    public ModelAndView updatePassword() {
        return new ModelAndView("forgetPassword");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public String login(HttpServletResponse res, String username, String password,String code, HttpServletRequest request) {
        System.out.println("登录的用户名是:"+username);
        //System.out.println("注册的密码是:"+);
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isEmpty(username)) {
            jsonObject.put("code", "1");
            jsonObject.put("message", "用户名不能为空");
            return jsonObject.toString();
        }
        if (StringUtils.isEmpty(password)) {
            jsonObject.put("code", "1");
            jsonObject.put("message", "密码不能为空");
            return jsonObject.toString();
        }
        if (StringUtils.isEmpty(code)) {
            jsonObject.put("code", "1");
            jsonObject.put("message", "验证码不能为空");
            return jsonObject.toString();
        }
        String cookieCode = CookieUtil.getCode(request);
        if(!cookieCode.equalsIgnoreCase(code)){
            jsonObject.put("code", "1");
            jsonObject.put("message", "验证码不正确");
            return jsonObject.toString();
        }

        UserEntity entity = userService.login(username,password);
        if (StringUtils.isEmpty(entity)) {
            jsonObject.put("code", "1");
            jsonObject.put("message", "用户不存在");
            return jsonObject.toString();
        }
        if (!MD5Util.verfityMd5(password, entity.getPassword())) {
            jsonObject.put("code", "1");
            jsonObject.put("message", "密码不正确");
            return jsonObject.toString();
        }
        try {
            CookieUtil.setLoginCookie(res, entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (entity.getRole().equalsIgnoreCase("2")) {
            jsonObject.put("code", "ADMIN");
        }
        jsonObject.put("message", "SUCCESS");
        jsonObject.put("code", "2");
        return jsonObject.toString();
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public String register(String name, String password, String phone) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isEmpty(name)) {
            jsonObject.put("code", "1");
            jsonObject.put("message", "用户名不能为空");
            return jsonObject.toString();
        }
        UserEntity entity = userService.queryOne(name);
        if (!StringUtils.isEmpty(entity)) {
            jsonObject.put("code", "1");
            jsonObject.put("message", "用户名已存在");
            return jsonObject.toString();
        }
        String result = userService.register(name, password, phone);
        if (result.equalsIgnoreCase("SUCCESS")) {
            jsonObject.put("code", "2");
            jsonObject.put("message", result);
        } else {
            jsonObject.put("code", "1");
            jsonObject.put("message", "error");
        }
        return jsonObject.toString();
    }
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public String updatePassword(String name, String password,String phone) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isEmpty(name)) {
            jsonObject.put("code", "1");
            jsonObject.put("message", "用户名不能为空");
            return jsonObject.toString();
        }
        UserEntity entity = userService.queryOne(name);
        if (StringUtils.isEmpty(entity)) {
            jsonObject.put("code", "1");
            jsonObject.put("message", "用户名不存在");
            return jsonObject.toString();
        }
        String result = userService.updatePassword(name, password, phone);
        if (result.equalsIgnoreCase("SUCCESS")) {
            jsonObject.put("code", "2");
            jsonObject.put("message", result);
        } else {
            jsonObject.put("message", "error");
            jsonObject.put("code", "1");
        }
        return jsonObject.toString();
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest req, HttpServletResponse res) {
        LoginAccountUtil.clearYzAdAccountModel();
        CookieUtil.deleteLoginCookie(res, req);
        return new ModelAndView("login");
    }
}
