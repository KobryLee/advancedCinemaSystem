package com.example.cinema.bl.management;

import com.example.cinema.vo.RefundForm;
import com.example.cinema.vo.ResponseVO;

/**
 * Created by AzureXH on 2019/6/8
 */
public interface RefundService {
    /**
     * 加一个退票策略
     * @param refundForm
     * @return
     */
    ResponseVO addRefundStrategy(RefundForm refundForm);

    /**
     * 得到所有退票策略
     * @return
     */
    ResponseVO getAllRefundStrategy();

    /**
     * 删除一个退票策略
     * @param name
     * @return
     */
    ResponseVO deleteRefundStrategyByName(String name);
}
