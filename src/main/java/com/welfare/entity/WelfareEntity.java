package com.welfare.entity;

import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/7/10 14:42
 * @Description:
 */
@Repository
@Table(name = "welfare")
public class WelfareEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "welfare_name")
    private String welfareName;
    @Column(name = "welfare_account")
    private long welfareAccount;
    @Column(name = "welfare_actual_account")
    private long welfareActualAccount;
    @Column(name = "welfare_sponsor")
    private String welfareSponsor;
    @Column(name = "welfare_sponsor_name")
    private String welfareSponsorName;
    @Column(name = "welfare_title")
    private String welfareTitle;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "start_time")
    private Date startTime;
    @Column(name = "end_time")
    private Date endTime;
    @Column(name = "context")
    private String context;
    @Column(name = "bu_hash")
    private String buHash;
    @Column(name = "state")
    private Integer state;
    @Column(name = "tag")
    private String tag;
    @Column(name = "image")
    private String image;
    @Column(name = "welfare_value")
    private BigDecimal welfareValue;

    private String style;
    private String ratio;
    private String peopleSize;

    public String getPeopleSize() {
        return peopleSize;
    }

    public void setPeopleSize(String peopleSize) {
        this.peopleSize = peopleSize;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWelfareName() {
        return welfareName;
    }

    public void setWelfareName(String welfareName) {
        this.welfareName = welfareName;
    }

    public long getWelfareAccount() {
        return welfareAccount;
    }

    public void setWelfareAccount(long welfareAccount) {
        this.welfareAccount = welfareAccount;
    }

    public long getWelfareActualAccount() {
        return welfareActualAccount;
    }

    public void setWelfareActualAccount(long welfareActualAccount) {
        this.welfareActualAccount = welfareActualAccount;
    }

    public String getWelfareSponsor() {
        return welfareSponsor;
    }

    public void setWelfareSponsor(String welfareSponsor) {
        this.welfareSponsor = welfareSponsor;
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

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getBuHash() {
        return buHash;
    }

    public void setBuHash(String buHash) {
        this.buHash = buHash;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getWelfareSponsorName() {
        return welfareSponsorName;
    }

    public void setWelfareSponsorName(String welfareSponsorName) {
        this.welfareSponsorName = welfareSponsorName;
    }

    public BigDecimal getWelfareValue() {
        return welfareValue;
    }

    public void setWelfareValue(BigDecimal welfareValue) {
        this.welfareValue = welfareValue;
    }
}
