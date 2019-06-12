package com.example.cinema.po;

import com.example.cinema.vo.RefundForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AzureXH on 2019/6/3
 */
public class RefundStrategy {

    /**
     * 退票策略的主键 无作用 只是作为主键
     */
    private Integer id;
    /**
     * 退票策略的名称
     */
    private String name;
    /**
     * 退票策略是否针对拥有会员卡用户  1：是 0：否
     */
    private Integer isVip;
    /**
     * 不可退票的时间点 可能为空
     */
    private String falseTime;
    /**
     * 退票的时间区间的起始时间
     */
    private String startTime;
    /**
     * 退票的时间区间的末尾时间
     */
    private String endTime;
    /**
     * 计算手续费的惩罚因子
     */
    private Double penalty;

    public RefundStrategy(){

    }
    public RefundStrategy(String name, Integer isVip, String falseTime, String startTime, String endTime, Double penalty) {
        this.name = name;
        this.isVip = isVip;
        this.falseTime = falseTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.penalty = penalty;
    }

    public List<RefundStrategy> convert2List(RefundForm refundForm){
        List<RefundStrategy> refundStrategies = new ArrayList<RefundStrategy>();
        int len = refundForm.getStartTime().size();
        String name = refundForm.getName();
        String falseTime = refundForm.getFalseTime();
        int isVip = refundForm.getIsVip();
        for (int i=0;i<len;i++){
            String startTime = refundForm.getStartTime().get(i);
            String endTime = refundForm.getEndTime().get(i);
            double penalty = refundForm.getPenalty().get(i);
            refundStrategies.add(new RefundStrategy(name,isVip,falseTime,startTime,endTime,penalty));
        }
        return refundStrategies;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Double getPenalty() {
        return penalty;
    }

    public void setPenalty(Double penalty) {
        this.penalty = penalty;
    }
}
