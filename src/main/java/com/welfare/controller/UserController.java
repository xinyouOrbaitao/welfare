package com.welfare.controller;

import com.github.pagehelper.PageInfo;
import com.welfare.entity.UserAccountEntity;
import com.welfare.entity.UserAccountLogEntity;
import com.welfare.entity.UserEntity;
import com.welfare.entity.vo.PageParam;
import com.welfare.service.BumoService;
import com.welfare.service.UserAccountService;
import com.welfare.service.UserService;
import com.welfare.util.LoginAccountUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/7/12 14:50
 * @Description:
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private BumoService bumoService;
    /**
     * 获取当前用户信息
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    public String get(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        jsonObject.put("user", userEntity);
        return jsonObject.toString();
    }

    /**
     * 修改用户信息
     * @param userEntity
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String update(UserEntity userEntity) {
        JSONObject jsonObject = new JSONObject();
        userService.update(userEntity);
        jsonObject.put("code", "SUCCESS");
        return jsonObject.toString();
    }

    /**
     * 查询账户信息
     * @return
     */
    @RequestMapping(value = "/selectAccount", method = RequestMethod.POST)
    @ResponseBody
    public String selectAccount(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        UserAccountEntity userAccountEntity = userAccountService.selectUserAccount(userEntity.getId());
        jsonObject.put("userAccountEntity", userAccountEntity);
        jsonObject.put("code", "SUCCESS");
        return jsonObject.toString();
    }

    /**
     * 捐赠
     *
     * @param welfareId
     * @param amount
     * @return
     */
    @RequestMapping(value = "/donate", method = RequestMethod.POST)
    @ResponseBody
    public String donate(String welfareId,String amount,HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        return userAccountService.donate(userEntity.getId(), welfareId,Integer.parseInt( amount)).toJSONString();

    }

    /**
     * 提现
     *
     * @param amount
     * @return
     */
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    @ResponseBody
    public String withdraw(int amount,HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        return userAccountService.withdraw(userEntity.getId(), amount).toJSONString();

    }

    /**
     * 充值
     *
     * @param amount
     * @return
     */
    @RequestMapping(value = "/recharge", method = RequestMethod.POST)
    @ResponseBody
    public String recharge(int amount,HttpServletRequest request) {
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        return userAccountService.recharge(userEntity.getId(), amount).toJSONString();
    }

    /**
     * 账号记录
     *
     * @return
     */
    @RequestMapping(value = "/selectLogList", method = RequestMethod.POST)
    @ResponseBody
    public String selectLogList(PageParam pageParam, String type,HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = LoginAccountUtil.getUserEntity(request);
        PageInfo<UserAccountLogEntity> list = userAccountService.selectLogList(userEntity.getId(), pageParam.getPageNo(), pageParam.getPageSize(), type);
        jsonObject.put("logList", list);
        jsonObject.put("code", "SUCCESS");
        return jsonObject.toString();
    }
    /**
     * 账号记录
     *
     * @return
     */
    @RequestMapping(value = "/queryHash", method = RequestMethod.POST)
    @ResponseBody
    public String queryHash(String hash,HttpServletRequest request) {
        System.out.println(hash);
//        String result = bumoService.queryHash(hash);
        String result = "{\n" +
                " \"total_count\":1,\n" +
                " \"transactions\":[{\n" +
                "  \"actual_fee\":\"312000\",\n" +
                "  \"close_time\":1578924283381788,\n" +
                "  \"error_code\":0,\n" +
                "  \"error_desc\":\"\",\n" +
                "  \"hash\":\"f2384bef657dc58c4ab6daa4ae4ab02637a95c5206e0419f268ab8462e8466ad\",\n" +
                "  \"ledger_seq\":99,\n" +
                "  \"signatures\":[{\n" +
                "   \"public_key\":\"\",\n" +
                "   \"sign_data\":\"e621b0fe192e6cf3846a95964be8f737abd2ee812734bfa06264cff643a3ee77dda187e011702d0cf75b75732f6b2e198d0124f73b9332f76587cc78f521fe09\"\n" +
                "  }],\n" +
                "  \"transaction\":{\n" +
                "   \"fee_limit\":1000000,\n" +
                "   \"gas_price\":1000,\n" +
                "   \"nonce\":1,\n" +
                "   \"operations\":[{\n" +
                "    \"create_account\":{\n" +
                "     \"dest_address\":\"buQh8sRTti3D7mpwVkKBg1RiUJLXfcRjzoCL\",\n" +
                "     \"init_balance\":100000000000,\n" +
                "     \"priv\":{\n" +
                "      \"master_weight\":\"1\",\n" +
                "      \"thresholds\":{\n" +
                "       \"tx_threshold\":1\n" +
                "      }\n" +
                "     }\n" +
                "    },\n" +
                "    \"metadata\":\"activate account\",\n" +
                "    \"source_address\":\"buQo18cwoNosY6WtmL4koCJ3uoBNhJyeejJD\",\n" +
                "    \"type\":1\n" +
                "   }],\n" +
                "   \"source_address\":\"buQo18cwoNosY6WtmL4koCJ3uoBNhJyeejJD\"\n" +
                "  },\n" +
                "  \"tx_size\":312\n" +
                " }]\n" +
                "}";


        return result;
    }

}
