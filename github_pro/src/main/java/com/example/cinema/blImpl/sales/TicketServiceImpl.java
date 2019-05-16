package com.example.cinema.blImpl.sales;

import com.example.cinema.bl.promotion.ActivityService;
import com.example.cinema.bl.sales.TicketService;
import com.example.cinema.blImpl.management.hall.HallServiceForBl;
import com.example.cinema.blImpl.management.schedule.ScheduleServiceForBl;
import com.example.cinema.blImpl.promotion.ActivityServiceForBl;
import com.example.cinema.blImpl.promotion.CouponServiceForBl;
import com.example.cinema.blImpl.promotion.VIPServiceForBl;
import com.example.cinema.data.promotion.ActivityMapper;
import com.example.cinema.data.promotion.CouponMapper;
import com.example.cinema.data.sales.TicketMapper;
import com.example.cinema.po.*;
import com.example.cinema.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
/**
 * Created by liying on 2019/4/16.
 */
@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    TicketMapper ticketMapper;
    @Autowired
    ScheduleServiceForBl scheduleService;
    @Autowired
    HallServiceForBl hallService;
    @Autowired
    CouponServiceForBl couponService;
    @Autowired
    ActivityServiceForBl activityServiceForBl;
    @Autowired
    VIPServiceForBl vipServiceForBl;


    @Override
    @Transactional
    public ResponseVO addTicket(TicketForm ticketForm) {
        try{


            int userId=ticketForm.getUserId();
            int scheduleId=ticketForm.getScheduleId();
            List<SeatForm> seats= ticketForm.getSeats();

            for(SeatForm s: seats){
                Ticket ticket= new Ticket();
                ticket.setUserId(userId) ;
                ticket.setScheduleId(scheduleId);
                ticket.setColumnIndex(s.getColumnIndex());
                ticket.setRowIndex(s.getRowIndex());
                ticket.setState(0);

                ticketMapper.insertTicket(ticket);
            }//对于每一张ticket都在数据库中添加一个ticket对象

            return ResponseVO.buildSuccess();}
        catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("选座失败！");
        }

    }

    @Override
    @Transactional
    public ResponseVO completeTicket(List<Integer> id, int couponId) {
        try{
            List<Ticket> tickets=new ArrayList<Ticket>();
            for(Integer i: id){
                tickets.add(ticketMapper.selectTicketById(i));
            }
            List<TicketVO> ticketVOS=new ArrayList<>();
            Coupon coupon=couponService.getCouponById(couponId);
            int movieId=tickets.get(0).getScheduleId();//通过ticket寻找movieId,由于多张tickets都只对应1部电影，因此只要取第一张ticket

            double total=0;
            Timestamp timestamp=tickets.get(0).getTime();

            for(Ticket t:tickets){
                int scheduleId=t.getScheduleId();
                ScheduleItem schedule=scheduleService.getScheduleItemById(scheduleId);
                double fare=schedule.getFare();

                ticketMapper.updateTicketState(t.getId(),1);   // 改变ticket状态为已购买（"1"）
                t.setState(1);

                TicketVO ticketVO=t.getVO();
                ticketVOS.add(ticketVO);//构造ticketVO后加入一个ticketVOS的列表
                total+=fare;
            }

            List<Activity> activities=activityServiceForBl.selectActivityByTimeAndMovie(timestamp,movieId);
            //根据时间和电影ID在数据库中寻找满足条件的活动
            List<Coupon> couponsToGive=new ArrayList<>();//构造根据活动赠送的优惠券列表
            for(Activity i:activities){
                couponsToGive.add(i.getCoupon());
            }//添加赠送的优惠券


            TicketWithCouponVO ticketWithCouponVO=new TicketWithCouponVO();
            ticketWithCouponVO.setCoupons(couponsToGive);
            ticketWithCouponVO.setTicketVOList(ticketVOS);
            ticketWithCouponVO.setTotal(total);
            //构造ticketWithCouponVO作为ResponseVO中的content

            if(coupon.getTargetAmount()<=total){
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
            ScheduleItem schedule=scheduleService.getScheduleItemById(scheduleId);
            Hall hall=hallService.getHallById(schedule.getHallId());
            int[][] seats=new int[hall.getRow()][hall.getColumn()];
            tickets.stream().forEach(ticket -> {
                seats[ticket.getRowIndex()][ticket.getColumnIndex()]=1;
            });
            ScheduleWithSeatVO scheduleWithSeatVO=new ScheduleWithSeatVO();
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
            return ResponseVO.buildSuccess(ticketMapper.selectTicketByUser(userId) );
        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败!");
        }
    }//需要重写的

    @Override
    @Transactional
    public ResponseVO completeByVIPCard(List<Integer> id, int couponId) {
        try{
            int userId=ticketMapper.selectTicketById(id.get(0)).getUserId();
            VIPCard vipCard=vipServiceForBl.selectCardByUserId(userId);
            Coupon coupon = couponService.getCouponById(couponId);

            List<Ticket> tickets=new ArrayList<>();
            int movieId=tickets.get(0).getScheduleId();
            for(int i:id){
                tickets.add(ticketMapper.selectTicketById(i));
            }
            List<TicketVO> ticketVOS=new ArrayList<>();
            Timestamp timestamp=tickets.get(0).getTime();
            double total=0;

            for(Ticket t: tickets){
                int scheduleId=t.getScheduleId();
                ScheduleItem schedule=scheduleService.getScheduleItemById(scheduleId);
                double fare=schedule.getFare();

                t.setState(1);
                ticketMapper.updateTicketState(t.getId(),1);   //--> 不缺定是否要改变ticket的状态

                TicketVO ticketVO=t.getVO();
                ticketVOS.add(ticketVO);
                total+=fare;
            }

            List<Activity> activities=activityServiceForBl.selectActivityByTimeAndMovie(timestamp,movieId);
            TicketWithCouponVO ticketWithCouponVO=new TicketWithCouponVO();

            List<Coupon> couponsToGive=new ArrayList<>();
            for(Activity i:activities){
                couponsToGive.add(i.getCoupon());
            }
            ticketWithCouponVO.setCoupons(couponsToGive);
            ticketWithCouponVO.setTicketVOList(ticketVOS);
            ticketWithCouponVO.setTotal(total);

            if(coupon.getTargetAmount()<=total){
                double Payment=total-coupon.getDiscountAmount();
                if(vipCard.getBalance()>=Payment){
                    vipCard.setBalance(vipCard.getBalance()-Payment);
                    ticketMapper.VIPPay(userId,Payment);//会员卡扣费
                    return ResponseVO.buildSuccess(ticketWithCouponVO);
                }
                else{
                    return ResponseVO.buildFailure("会员卡余额不足！");
                }
            }
            else{
                return ResponseVO.buildFailure("总额未达到优惠券使用门槛");

            }


        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }

    }

    @Override
    public ResponseVO cancelTicket(List<Integer> id) {
        try {
            for (int i: id){
                ticketMapper.updateTicketState(i,2);
            }
            return ResponseVO.buildSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败!");
        }

    }//取消锁座

    /*public ResponseVO checkCoupon(int couponId,Timestamp timestamp, int total){
        try {
            Coupon coupon=couponService.getCouponById(couponId);
            if(timestamp.before(coupon.getEndTime()) && timestamp.after(coupon.getStartTime()) && total>=coupon.getDiscountAmount()){
                return ResponseVO.buildSuccess(coupon);
            }
            else{
                return ResponseVO.buildFailure("It's not a validated coupon");
            }
        }catch (Exception e){
            return ResponseVO.buildFailure("Something went wrong!");
        }

    }*/




}
