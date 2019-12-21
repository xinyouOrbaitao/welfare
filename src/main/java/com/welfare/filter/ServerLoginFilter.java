package com.welfare.filter;

import com.welfare.entity.UserEntity;
import com.welfare.util.CookieUtil;
import com.welfare.util.LoginAccountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/8/20 10:20
 * @Description:
 */
public class ServerLoginFilter implements Filter {
    private Logger logger = LoggerFactory.getLogger(ServerLoginFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = null;
        HttpServletResponse response = null;
        try {
            request = (HttpServletRequest) servletRequest;
            response = (HttpServletResponse) servletResponse;

            String url = request.getServletPath();
            //请求跳过，不需要权限验证
            if (isSkip(url)) {
                filterChain.doFilter(request, response);
                return;
            }

//            // 登录过滤
            Map checkResult = handleBossRequest(request, response);
            if ("true".equalsIgnoreCase(checkResult.get("success").toString())) {
                filterChain.doFilter(request, response);
            } else {
                response.sendRedirect("/login");
            }
        } catch (Exception e) {
            logger.error("登录异常",e);
            Map<String, String> map = new HashMap<>();
            map.put("message", "登录异常");
            response.sendRedirect("/login");
        }

    }

    /*******************私有方法*******************************/
    /**
     * 是否跳过权限验证
     *
     * @param url
     * @return
     */
    private boolean isSkip(String url) {
        if (url.endsWith("/login")
                || url.endsWith("/register")
                || url.endsWith("/index")
                || url.endsWith("/")
                || url.endsWith("/file/down")
                || url.endsWith(".js")
                || url.endsWith(".css")
                || url.endsWith(".jpg")
                || url.endsWith(".png")
                || url.endsWith(".ico")

        ) {
            return true;
        }
        return false;
    }

    /**
     * 校验是否登录
     *
     * @param request
     * @param response
     */
    private Map<String, Object> handleBossRequest(HttpServletRequest request, HttpServletResponse response) throws BadPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ServletException {
        Map<String, Object> map = new HashMap<>();
        /*
         * 	从cookie中获取登录信息
         */
        UserEntity loginAccount = CookieUtil.getLoginAccountFromCookie(request);
        if (loginAccount == null) {
            map.put("success", "false");
            map.put("message", "未登录");
        } else {
            map.put("success", "true");
            map.put("user", loginAccount);
            LoginAccountUtil.setUserEntity(loginAccount);
        }
        return map;
    }
}
