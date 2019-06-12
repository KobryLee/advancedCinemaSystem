package com.example.cinema.vo;
import java.util.List;

/**
 * Created by AzureXH on 2019/6/3
 */
public class RefundForm {

    /**
     * 退票策略名称
     */
    private String name;
    /**
     * 是否为针对拥有会员卡用户的策略 1：是  0：否
     */
    private Integer isVip;

    /**
     * 不可退票时间
     */
    private String falseTime;
    /**
     * 计算退票手续费的时间区间     直接用两个List   不使用Map
     */
    private List<String> startTime;
    private List<String> endTime;
    /**
     * 各个时间段退票所收的百分比
     */
    private List<Double> penalty;

    public RefundForm(){

    }

    public RefundForm(String name, Integer isVip, String falseTime, List<String> startTime, List<String> endTime, List<Double> penalty) {
        this.name = name;
        this.isVip = isVip;
        this.falseTime = falseTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.penalty = penalty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsVip() {
        return isVip;
    }

    public void setIsVip(Integer isVip) {
        this.isVip = isVip;
    }

    public String getFalseTime() {
        return falseTime;
    }

    public void setFalseTime(String falseTime) {
        this.falseTime = falseTime;
    }

    public List<String> getStartTime() {
        return startTime;
    }

    public void setStartTime(List<String> startTime) {
        this.startTime = startTime;
    }

    public List<String> getEndTime() {
        return endTime;
    }

    public void setEndTime(List<String> endTime) {
        this.endTime = endTime;
    }

    public List<Double> getPenalty() {
        return penalty;
    }

    public void setPenalty(List<Double> penalty) {
        this.penalty = penalty;
    }
}
