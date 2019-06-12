package com.example.cinema.blImpl.sales;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.cinema.bl.promotion.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cinema.bl.sales.TicketService;
import com.example.cinema.blImpl.management.hall.HallServiceForBl;
import com.example.cinema.blImpl.management.schedule.MovieServiceForBl;
import com.example.cinema.blImpl.management.schedule.ScheduleServiceForBl;
import com.example.cinema.blImpl.promotion.ActivityServiceForBl;
import com.example.cinema.blImpl.promotion.CouponServiceForBl;
import com.example.cinema.blImpl.promotion.VIPServiceForBl;
import com.example.cinema.data.sales.TicketMapper;
import com.example.cinema.po.*;
import com.example.cinema.vo.*;

/**
 * Created by liying on 2019/4/16.
 */
@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    TicketMapper ticketMapper;
    @Autowired
    MovieServiceForBl movieService;
    @Autowired
    ScheduleServiceForBl scheduleService;
    @Autowired
    HallServiceForBl hallService;
    @Autowired
    CouponServiceForBl couponService;
    @Autowired
    ActivityServiceForBl activityService;
    @Autowired
    VIPServiceForBl vipService;

    @Override
    @Transactional
    public ResponseVO addTicket(TicketForm ticketForm) {
    	try{
            int userId=ticketForm.getUserId();
            int scheduleId=ticketForm.getScheduleId();
            List<SeatForm> seats = ticketForm.getSeats();
            List<Integer> tickeIdList = new ArrayList<Integer>();
            
            for(SeatForm s: seats){
                Ticket ticket= new Ticket();
                ticket.setUserId(userId) ;
                ticket.setScheduleId(scheduleId);
                ticket.setColumnIndex(s.getColumnIndex());
                ticket.setRowIndex(s.getRowIndex());
                ticket.setState(0);
                ticket.setPaymentMode(-1);
                ticketMapper.insertTicket(ticket);

                tickeIdList.add(ticket.getId());
            }//对于每一张ticket都在数据库中添加一个ticket对象

            return ResponseVO.buildSuccess(tickeIdList);
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("选座失败！");
        }
    }

    @Override
    @Transactional
    public ResponseVO completeTicket(List<Integer> id, int couponId) {
    	try{
            List<Ticket> tickets = new ArrayList<Ticket>();
            for(Integer i: id){
                tickets.add(ticketMapper.selectTicketById(i));
            }
            List<TicketVO> ticketVOs = new ArrayList<TicketVO>();
            Coupon coupon=new Coupon();
            if(couponId==-1){
                coupon = couponService.getCouponById(couponId);
            }

            int movieId = tickets.get(0).getScheduleId(); //通过ticket寻找movieId，由于多张tickets都只对应1部电影，因此只要取第一张ticket
            int userId = tickets.get(0).getUserId();

            double total = 0;
            Timestamp timestamp = tickets.get(0).getTime();

            for(Ticket t: tickets){
                int scheduleId = t.getScheduleId();
                ScheduleItem schedule = scheduleService.getScheduleItemById(scheduleId);
                double fare = schedule.getFare();

                ticketMapper.updateTicketState(t.getId(), 1);   // 改变ticket状态为已购买（"1"）
                ticketMapper.updatePaymentMode(t.getId(),0);  //改变ticket的购买方式为银行卡支付（0）
                if(couponId!=-1){
                    ticketMapper.updateTicketCoupon(t.getId(),couponId);
                }
                t.setState(1);

                TicketVO ticketVO = t.getVO();
                ticketVOs.add(ticketVO);  //构造ticketVO后加入一个ticketVOS的列表
                total += fare;
            }

            List<Activity> activities = activityService.selectActivityByTimeAndMovie(timestamp, movieId);
            //根据时间和电影ID在数据库中寻找满足条件的活动

            List<Coupon> couponsToGive=new ArrayList<Coupon>();  //构造根据活动赠送的优惠券列表
            for(Activity i:activities){

                if(!couponService.existCouponUser(i.getCoupon().getId(), userId)){
                    couponsToGive.add(i.getCoupon());
                    ticketMapper.addCoupon(i.getCoupon().getId(), userId);
                }

            }//添加赠送的优惠券


            TicketWithCouponVO ticketWithCouponVO = new TicketWithCouponVO();
            ticketWithCouponVO.setCoupons(couponsToGive);
            ticketWithCouponVO.setTicketVOList(ticketVOs);
            ticketWithCouponVO.setTotal(total);
            //构造ticketWithCouponVO作为ResponseVO中的content

            if(couponId!=-1 && coupon.getTargetAmount()<=total){
                couponService.deleteCoupon(couponId,userId);
                return ResponseVO.buildSuccess(ticketWithCouponVO);

            }
            else if(couponId==-1){
                return ResponseVO.buildSuccess(ticketWithCouponVO);
            }//校验优惠券（默认前端已经做好根据时间筛选优惠券的操作，即这里选择的优惠券是在优惠期限以内的）
            return ResponseVO.buildFailure("总额低于门槛");

        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }//返回的是一个TicketWithCouponVO,里面包含了js文件里面的TicketVOList、没有优惠后的总价total，以及赠送的coupons列表

    @Override
    public ResponseVO getBySchedule(int scheduleId) {
        try {
            List<Ticket> tickets = ticketMapper.selectTicketsBySchedule(scheduleId);
            ScheduleItem schedule = scheduleService.getScheduleItemById(scheduleId);
            Hall hall = hallService.getHallById(schedule.getHallId());
            int[][] seats=new int[hall.getRow()][hall.getColumn()];
            tickets.stream().forEach(ticket -> {
                seats[ticket.getRowIndex()][ticket.getColumnIndex()]=1;
            });
            ScheduleWithSeatVO scheduleWithSeatVO = new ScheduleWithSeatVO();
            scheduleWithSeatVO.setScheduleItem(schedule);
            scheduleWithSeatVO.setSeats(seats);
            return ResponseVO.buildSuccess(scheduleWithSeatVO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public ResponseVO getTicketByUser(int userId) {
    	try {
    		List<Ticket> tickets = ticketMapper.selectTicketByUser(userId);
    		List<TicketForm> ticketForms = new ArrayList<TicketForm>();
    		tickets.stream().forEach(ticket -> {
				int scheduleId = ticket.getScheduleId();
				ScheduleItem scheduleItem = scheduleService.getScheduleItemById(scheduleId);
				TicketForm ticketForm = new TicketForm(userId, scheduleId, scheduleItem);
				ticketForm.addSeat(ticket.getColumnIndex(), ticket.getRowIndex());
				ticketForm.setState(ticket.getState());
				ticketForms.add(ticketForm);
    		});
    		
    		return ResponseVO.buildSuccess(ticketForms);
        }
    	catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    @Transactional
    public ResponseVO completeByVIPCard(List<Integer> id, int couponId) {
    	try{
            Coupon coupon = couponService.getCouponById(couponId);
            List<Ticket> tickets = new ArrayList<Ticket>();

            for(int i: id){
                tickets.add(ticketMapper.selectTicketById(i));
            }
            int movieId=tickets.get(0).getScheduleId();
            int userId=tickets.get(0).getUserId();
            VIPCard vipCard = vipService.selectCardByUserId(userId);
            List<TicketVO> ticketVOS=new ArrayList<>();
            Timestamp timestamp=tickets.get(0).getTime();
            double total=0;

            for(Ticket t: tickets){
                int scheduleId=t.getScheduleId();
                ScheduleItem schedule=scheduleService.getScheduleItemById(scheduleId);
                double fare=schedule.getFare();
                total+=fare;
            }

            List<Activity> activities = activityService.selectActivityByTimeAndMovie(timestamp, movieId);
            TicketWithCouponVO ticketWithCouponVO=new TicketWithCouponVO();

            List<Coupon> couponsToGive=new ArrayList<>();
            for(Activity i:activities){
                if(!couponService.existCouponUser(i.getCoupon().getId(),userId)){
                    ticketMapper.addCoupon(i.getCoupon().getId(),userId);
                    couponsToGive.add(i.getCoupon());
                }
            }


            

            double payment;
            double discountAmount = 0;
            double targetAmount = 0;

            if(coupon!=null){
                discountAmount = coupon.getDiscountAmount();
                targetAmount = coupon.getTargetAmount();
            }
            
            if(targetAmount <= total) {
                payment = total - discountAmount;
            }
            else{
                System.out.println("总额未达到优惠券使用门槛"); 
                payment = total;
            }
            
            if(vipCard.getBalance() >= payment){
                vipCard.setBalance(vipCard.getBalance() - payment);
                ticketMapper.VIPPay(userId,payment);  //会员卡扣费
                couponService.deleteCoupon(couponId,userId);
                System.out.println("付费成功");
                for (Ticket t : tickets) {
                    t.setState(1);
                    ticketMapper.updateTicketState(t.getId(),1);
                    ticketMapper.updatePaymentMode(t.getId(),1);  //改变ticket的购买方式为会员卡支付（1）
                    if(couponId!=-1){
                        ticketMapper.updateTicketCoupon(t.getId(),couponId);
                    } //更新ticket使用的couponId
                    TicketVO ticketVO = t.getVO();
                    ticketVOS.add(ticketVO);
                }
                ticketWithCouponVO.setCoupons(couponsToGive);
                ticketWithCouponVO.setTicketVOList(ticketVOS);
                ticketWithCouponVO.setTotal(total);
                return ResponseVO.buildSuccess(ticketWithCouponVO);
            }
            else{
                return ResponseVO.buildFailure("支付失败");
            }
        }
    	catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public ResponseVO cancelTicket(List<Integer> id) {
    	 try {
             for (int i: id) {
                 ticketMapper.updateTicketState(i, 2);
             }
             return ResponseVO.buildSuccess();
         }
    	 catch (Exception e) {
             e.printStackTrace();
             return ResponseVO.buildFailure("失败!");
         }
    }

    @Override
    public ResponseVO getTicketRefund(List<Integer> ticketId){
        try {
            List<Ticket> tickets=new ArrayList<Ticket>();
            for(int i:ticketId){
                tickets.add(ticketMapper.selectTicketById(i));
            }
            int userId=tickets.get(0).getUserId();  //得到用户ID
            int movieId=tickets.get(0).getScheduleId(); // 得到电影ID
            int couponId=tickets.get(0).getCouponId(); //得到ticket使用的优惠券ID
            Timestamp timestamp=tickets.get(0).getTime(); //得到电影购买的时间
            int paymentMode=tickets.get(0).getPaymentMode(); //得到购买的方式（0: 银行卡; 1: 会员卡支付f）
            int scheduleId=tickets.get(0).getScheduleId();
            ScheduleItem schedule=scheduleService.getScheduleItemById(scheduleId); // 得到电影的排片信息（主要是取得排片对应的票价）
            double penalty=0;//这里是假数据，之后的过程中要从数据库中获得penalty的比例
            int penaltyMode=isVip(userId); // 得到罚金的模式（vip是1，非vip是0）
            List<RefundStrategy> refundStrategies=ticketMapper.getRefundStrategies(penaltyMode); // 得到退票的罚金策略（根据罚金模式不同而不同）
            Date currentTime= new Date(); //得到现在的时间
            Date onTime=schedule.getStartTime();  //得到电影的开场时间
            double beforeOn=(double)(currentTime.getTime()-onTime.getTime())/(double)(1000*60); //计算现在离开场的时间还有多久
            for(RefundStrategy rs:refundStrategies){
                double startTime=Double.parseDouble(rs.getStartTime().split(":")[0])*60+
                        Double.parseDouble(rs.getStartTime().split(":")[1]);
                double endTime=Double.parseDouble(rs.getEndTime().split(":")[0])*60+
                        Double.parseDouble(rs.getEndTime().split(":")[1]);
                double falseTime=Double.parseDouble(rs.getFalseTime().split(":")[0])*60+
                        Double.parseDouble(rs.getFalseTime().split(":")[1]);

                if(beforeOn>=falseTime){   //判断是否已经超过可退票的最低时限
                    if(beforeOn> startTime && beforeOn<=endTime){
                        penalty=rs.getPenalty();
                        break;             //判断退票时间段处于可退票的哪个时间段内并且得到罚金比例
                    }
                }
                else{
                    return ResponseVO.buildFailure("已经超过可退票的最低时限，退票失败");
                }
            }
            double fare=0;
            double discountByCoupon=0;
            for(Ticket t:tickets){
                fare=fare+schedule.getFare(); //得到票价
                ticketMapper.updateTicketState(t.getId(),2);//删除购买记录，本质上是把电影票的支付状态改成已失效（2）
            }
            discountByCoupon= couponService.getCouponById(couponId).getDiscountAmount();
            fare=fare-discountByCoupon;
            double refund=(1-penalty)*fare;  //计算要退的金额
            switch(paymentMode){
                case 1:
                {
                    ticketMapper.VIPRefund(userId,refund);
                    break;    //退款
                }
            }
            List<Activity> activities = activityService.selectActivityByTimeAndMovie(timestamp, movieId);
            List<Coupon> couponsToCatch=new ArrayList<Coupon>();
            for(Activity i:activities){
                if(!couponService.existCouponUser(i.getCoupon().getId(), userId)){
                    couponsToCatch.add(i.getCoupon());
                }
            }//构造根据活动需要拿回的优惠券列表

            if(couponsToCatch.size()!=0){
                for(Coupon coupon:couponsToCatch){
                    couponService.deleteCoupon(coupon.getId(),userId);
                }
            }//删除已经给出的用户优惠券（在coupon_user表中删除）
            return ResponseVO.buildSuccess("退票成功");

        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("退票失败");
        }
    }

    private int isVip(int userId){
        if(ticketMapper.isVip(userId).size()!=0){
            return 1;
        }
        else{
            return 0;
        }
    }


}
