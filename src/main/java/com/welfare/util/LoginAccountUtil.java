package com.welfare.util;


import com.welfare.entity.UserEntity;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class LoginAccountUtil {
    private static final ThreadLocal<UserEntity> loginAccountInfo = new ThreadLocal<>();

    public static void setUserEntity(UserEntity account) {
        loginAccountInfo.set(account);
    }

    public static void clearYzAdAccountModel() {
        loginAccountInfo.remove();
    }

    public static UserEntity getUserEntity(HttpServletRequest request) {
        UserEntity loginAccount = loginAccountInfo.get();
        if (loginAccount == null) {
            try {
                return CookieUtil.getLoginAccountFromCookie(request);
            }catch (Exception e){

            }
            return null;
        }
        return loginAccount;
    }


    /**
     * 获取登录用户id
     *
     * @return
     */
    public static Long getLoginUserId() {
        UserEntity loginAccount = loginAccountInfo.get();
        if (loginAccount == null) {
            return null;
        }
        return loginAccount.getId();
    }

    public static String getLoginUserName() {
        UserEntity loginAccount = loginAccountInfo.get();
        if (loginAccount == null) {
            return "";
        }
        return loginAccount.getUsername();
    }
    /**
     * 定义图片的width
     */
    private static int width = 80;
    /**
     * 定义图片的height
     */
    private static int height = 20;
    /**
     * 定义图片上显示验证码的个数
     */
    private static int codeCount = 4;
    private static int xx = 15;
    private static int fontHeight = 18;
    private static int codeY = 16;
    private static char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static BufferedImage generateCodeAndPic(String code) {
        char[] arr = code.toCharArray();
        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics gd = buffImg.getGraphics();
        Random random = new Random();
        // 将图像填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, width, height);
        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
        // 设置字体。
        gd.setFont(font);
        // 画边框。
        gd.setColor(new Color(239,237,242));
        gd.drawRect(0, 0, width - 1, height - 1);
        // 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
        gd.setColor(new Color(44, 44, 44));
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            gd.drawLine(x, y, x + xl, y + yl);
        }
        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < arr.length; i++) {
            // 得到随机产生的验证码数字。
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(44, 44, 44));
            gd.drawString(String.valueOf(arr[i]), (i + 1) * xx, codeY);
            // 将产生的四个随机数组合在一起。
        }
        //存放生成的验证码BufferedImage对象
        return buffImg;
    }

    public static String getCode() {
        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuilder randomCode = new StringBuilder();
        // 创建一个随机数生成器类
        Random random = new Random();
        for (int i = 0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。
            String code = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
            randomCode.append(code);
        }
        return randomCode.toString();
    }
}
