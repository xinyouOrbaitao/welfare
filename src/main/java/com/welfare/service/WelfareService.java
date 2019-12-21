package com.welfare.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.welfare.entity.WelfareEntity;

import java.util.List;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/7/10 15:01
 * @Description:
 */
public interface WelfareService {
    /**
     * 添加项目
     *
     * @param entity
     */
    public void save(WelfareEntity entity);

    /**
     * 修改项目
     *
     * @param entity
     */
    public void update(WelfareEntity entity);

    /**
     * 审核项目状态
     *
     * @param welfareId
     */
    public void updateStatus(String welfareId, String state);

    /**
     * 管理员查询公益项目列表
     *
     * @param pageNo
     * @param pageSize
     * @param type
     * @return
     */
    public PageInfo<WelfareEntity> selectListByAdmin(int pageNo, int pageSize, String type);

    /**
     * 用户查询项目
     *
     * @param pageNo
     * @param pageSize
     * @param userId
     * @return
     */
    public PageInfo<WelfareEntity> selectListByUser(int pageNo, int pageSize, String userId);

    public List<WelfareEntity> selectListByUser(String userId);

    public PageInfo<WelfareEntity> selectListByIndex(int pageNo, int pageSize);

    public List<WelfareEntity> selectListToThree();

    public JSONObject settlement(String welfareId);

    public WelfareEntity selectById(String id);

    public int totalAmount();

    public int totalPeople();

    public int welfarePeopleSize(String id);

    public List<WelfareEntity> selectListByIdList(List<Long> list);
}
