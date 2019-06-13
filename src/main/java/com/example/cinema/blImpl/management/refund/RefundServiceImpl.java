package com.example.cinema.blImpl.management.refund;

import com.example.cinema.bl.management.RefundService;
import com.example.cinema.data.management.RefundMapper;
import com.example.cinema.po.RefundStrategy;
import com.example.cinema.vo.RefundForm;
import com.example.cinema.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AzureXH on 2019/6/8
 */
@Service
public class RefundServiceImpl implements RefundService {
    @Autowired
    private RefundMapper refundMapper;
    @Override
    public ResponseVO addRefundStrategy(RefundForm refundForm) {
        List<RefundStrategy> refundStrategies = new RefundStrategy().convert2List(refundForm);
        refundMapper.insertRefundStrategy(refundStrategies);
        return ResponseVO.buildSuccess();
    }

    @Override
    public ResponseVO getAllRefundStrategy() {
        try{

            List<RefundStrategy> refundStrategyList = refundMapper.selectAllRefundStrategy();
            List<RefundForm> refundFormList = refundPOList2refundVOList(refundStrategyList);
            return ResponseVO.buildSuccess(refundFormList);
        }catch(IndexOutOfBoundsException e){
            //数据为空只要返回空值就好了
            return  ResponseVO.buildSuccess();
        }
    }

    @Override
    public ResponseVO deleteRefundStrategyByName(String name) {
        return ResponseVO.buildSuccess(refundMapper.deleteRefundStrategyByName(name));
    }

    private List<RefundForm> refundPOList2refundVOList(List<RefundStrategy> refundStrategyList){
        System.out.println(refundStrategyList);
        List<RefundForm> resultList = new ArrayList<RefundForm>();
        List<String> startTimeList = new ArrayList<String>();
        List<String> endTimeList = new ArrayList<String>();
        List<Double> penaltyList = new ArrayList<Double>();
        for(int i=0;i<refundStrategyList.size()-1;i++){
            RefundStrategy r1 = refundStrategyList.get(i);
            RefundStrategy r2 = refundStrategyList.get(i+1);

            startTimeList.add(r1.getStartTime());
            endTimeList.add(r1.getEndTime());
            penaltyList.add(r1.getPenalty());
            //前后两个策略是不同的
            if(!r1.getName().equals(r2.getName())){
                //前后策略不同 可以向结果List中添加新的RefundForm
                //获取参数
                String name = r1.getName();
                int isVip = r1.getIsVip();
                String falseTime = r1.getFalseTime();

                RefundForm refundForm = new RefundForm(name,isVip,falseTime,startTimeList,endTimeList,penaltyList);
                resultList.add(refundForm);

                //清除列表 重新添加
                startTimeList = new ArrayList<String>();
                endTimeList = new ArrayList<String>();
                penaltyList = new ArrayList<Double>();

            }
        }
        //对末尾策略的处理
        //无论末尾策略是否和上一个策略名称相同 操作都是一样的
        int listSize = refundStrategyList.size();
        RefundStrategy r1 = refundStrategyList.get(listSize-1);
        //获取参数
        String name = r1.getName();
        int isVip = r1.getIsVip();
        String falseTime = r1.getFalseTime();
        startTimeList.add(r1.getStartTime());
        endTimeList.add(r1.getEndTime());
        penaltyList.add(r1.getPenalty());

        RefundForm refundForm = new RefundForm(name,isVip,falseTime,startTimeList,endTimeList,penaltyList);
        resultList.add(refundForm);

        return resultList;
    }
}
