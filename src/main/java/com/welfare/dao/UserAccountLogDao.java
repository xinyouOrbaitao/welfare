package com.welfare.dao;

import com.welfare.MyMapper;
import com.welfare.entity.UserAccountLogEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author ：chenxinyou.
 * @Title :
 * @Date ：Created in 2019/8/25 14:24
 * @Description:
 */
@Mapper
public interface UserAccountLogDao extends MyMapper<UserAccountLogEntity> {

    @Results(id = "queryAccountLog", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "type", column = "type"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "welfareId", column = "welfare_id"),
            @Result(property = "welfareName", column = "welfare_name"),
            @Result(property = "username", column = "username"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "hash", column = "hash")

    })
    @Select("<script> " +
            "select * from user_account_log " +
            "<where>" +
            "<if test = 'type!=null and type!=0 '> " +
            " and type = #{type,jdbcType=INTEGER}" +
            "</if>" +
            "<if test = 'userId!=null and userId!=0 '> " +
            " and user_id = #{userId,jdbcType=INTEGER}" +
            "</if>" +
            "</where>" +
            "</script>"
    )
    public List<UserAccountLogEntity> selectListByState(@Param("type") Integer type, @Param("userId")long userId);

    @ResultMap(value = "queryAccountLog")
    @Select("<script> " +
            "select * from user_account_log " +
            "<where>" +
            "<if test = 'type!=null and type!=0 '> " +
            " and type = #{type,jdbcType=INTEGER}" +
            "</if>" +
            "<if test = 'welfareId!=null '> " +
            " and welfare_id = #{welfareId,jdbcType=VARCHAR}" +
            "</if>" +
            "</where>" +
            "</script>"
    )
    public List<UserAccountLogEntity> selectListByWelfareId(@Param("type") Integer type, @Param("welfareId")String welfareId);

    @Select("SELECT SUM(amount) FROM user_account_log WHERE type = 2 ")
    public Integer selectTotalAmount();

    @Select("SELECT count(*) FROM user_account_log WHERE type = 2 ")
    public Integer selectTotalPeople();

    @Select("SELECT count(*) FROM user_account_log WHERE welfare_id = #{id,jdbcType=VARCHAR} ")
    public Integer selectTotalPeopleByWelfare(@Param("id")String  id);
}
