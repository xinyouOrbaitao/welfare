package com.welfare.dao;

import com.welfare.MyMapper;
import com.welfare.entity.BumoEntity;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * @Author ：chenxinyou.
 * @Title :
 * @Date ：Created in 2019/9/19 15:10
 * @Description:
 */
public interface BumoDao extends MyMapper<BumoEntity> {
    @Results(id = "queryBumo", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "address", column = "address"),
            @Result(property = "publicKey", column = "publicKey"),
            @Result(property = "txHash", column = "txHash"),
            @Result(property = "privateKey", column = "privateKey")
    })
    @Select("select * from bumo where user_id =#{userId,jdbcType=VARCHAR}")
    BumoEntity selectByUserId(long userId);

}
