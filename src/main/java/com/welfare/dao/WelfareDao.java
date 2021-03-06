package com.welfare.dao;

import com.welfare.MyMapper;
import com.welfare.entity.WelfareEntity;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/7/10 14:48
 * @Description:
 */
@Mapper
public interface WelfareDao extends MyMapper<WelfareEntity> {
    /**
     * 修改项目状态
     *
     * @param welfareId
     * @param state
     * @return
     */
    @Update("update welfare set state = #{state,jdbcType=VARCHAR},welfare_value = #{welfareValue} where id =  #{welfareId,jdbcType=VARCHAR}")
    public int updateStatus(@Param("welfareId") String welfareId, @Param("state") String state,
                            @Param("welfareValue")BigDecimal welfareValue);

    @Results(id = "query", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "welfareName", column = "welfare_name"),
            @Result(property = "welfareAccount", column = "welfare_account"),
            @Result(property = "welfareActualAccount", column = "welfare_actual_account"),
            @Result(property = "welfareSponsor", column = "welfare_sponsor"),
            @Result(property = "welfareTitle", column = "welfare_title"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "endTime", column = "end_time"),
            @Result(property = "context", column = "context"),
            @Result(property = "buHash", column = "bu_hash"),
            @Result(property = "state", column = "state"),
            @Result(property = "image", column = "image"),
            @Result(property = "tag", column = "tag")
    })
    @Select("<script> " +
            "select * from welfare " +
            "<where>" +
            "<if test = 'state!=null and state!=0 '> " +
            " state = #{state,jdbcType=INTEGER}" +
            "</if>" +
            "</where>" +
            "</script>"
    )
    public List<WelfareEntity> selectListByState(@Param("state") Integer state);

    @Select("select * from welfare where welfare_sponsor=#{welfareSponsor,jdbcType=VARCHAR} order by end_time asc ")
    public List<WelfareEntity> selectListByUserId(@Param("welfareSponsor") String userId);

    @ResultMap(value = "query")
    @Select("select * from welfare where id=#{id,jdbcType=VARCHAR}")
    public WelfareEntity selectWelfareOne(@Param("id") String id);

    @ResultMap(value = "query")
    @Select("select * FROM welfare WHERE state = 2 ORDER BY create_time DESC limit 3")
    public List<WelfareEntity> selectWelfareByThree();

    @Update("update welfare set " +
            " welfare_actual_account =#{welfareActualAccount,jdbcType=INTEGER}" +
            " where id=#{id,jdbcType=VARCHAR}")
    public int updateWelfareAccount(@Param("id") long id,
                                    @Param("welfareActualAccount") long welfareActualAccount);


    @ResultMap(value = "query")
    @Select("<script> " +
            "select * from welfare " +
            "<where>" +
            "<if test = 'list!=null '> " +
            " id in <foreach collection='list' item='id' open='(' separator=',' close = ')'>" +
            "  #{id} " +
            "</foreach>" +
            "</if>" +
            "</where>" +
            "</script>"
    )
    public List<WelfareEntity> selectListByIdList(@Param("list") List<Long> list);
}
