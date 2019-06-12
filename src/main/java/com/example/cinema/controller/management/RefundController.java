package com.example.cinema.controller.management;

import com.example.cinema.bl.management.RefundService;
import com.example.cinema.vo.RefundForm;
import com.example.cinema.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AzureXH on 2019/6/8
 */
@RestController
public class RefundController {
    @Autowired
    private RefundService refundService;
    @RequestMapping(value = "/refund/add",method = RequestMethod.POST)
    public ResponseVO addRefundStrategy(@RequestBody RefundForm refundForm){
        return refundService.addRefundStrategy(refundForm);
    }
    @RequestMapping(value = "/refund/manage",method = RequestMethod.GET)
    public ResponseVO getAllRefundStrategy() {
        return refundService.getAllRefundStrategy();
    }

    @RequestMapping(value = "/refund/delete/{name}", method = RequestMethod.GET)
    public ResponseVO deleteRefundStrategy(@PathVariable String name){
        return refundService.deleteRefundStrategyByName(name);
    }

}
