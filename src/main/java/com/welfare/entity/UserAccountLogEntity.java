package com.welfare.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/8/25 14:25
 * @Description:
 */
@Table(name = "user_account_log")
public class UserAccountLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "username")
    private String username;
    @Column(name = "type")
    private String type;
    @Column(name = "amount")
    private long amount;
    @Column(name = "creat_time")
    private Date createTime;
    @Column(name = "welfare_id")
    private String welfareId;
    @Column(name = "welfare_name")
    private String welfareName;
    @Column(name = "hash")
    private String hash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getWelfareId() {
        return welfareId;
    }

    public void setWelfareId(String welfareId) {
        this.welfareId = welfareId;
    }

    public String getWelfareName() {
        return welfareName;
    }

    public void setWelfareName(String welfareName) {
        this.welfareName = welfareName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
