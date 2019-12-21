package com.welfare.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/7/10 14:42
 * @Description:
 */
@Table(name = "welfare_log")
public class WelfareLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id ;
    @Column(name = "welfare_sponsor")
    private String welfareSponsor;
    @Column(name = "welfare_sponsor_name")
    private String welfareSponsorName;
    @Column(name = "welfare_id")
    private long welfareId;
    @Column(name = "welfare_title")
    private String welfareTitle;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "code")
    private String code;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWelfareSponsor() {
        return welfareSponsor;
    }

    public void setWelfareSponsor(String welfareSponsor) {
        this.welfareSponsor = welfareSponsor;
    }

    public long getWelfareId() {
        return welfareId;
    }

    public void setWelfareId(long welfareId) {
        this.welfareId = welfareId;
    }

    public String getWelfareTitle() {
        return welfareTitle;
    }

    public void setWelfareTitle(String welfareTitle) {
        this.welfareTitle = welfareTitle;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWelfareSponsorName() {
        return welfareSponsorName;
    }

    public void setWelfareSponsorName(String welfareSponsorName) {
        this.welfareSponsorName = welfareSponsorName;
    }
}
