package com.example.cinema.blImpl.sales;

import com.example.cinema.bl.promotion.ActivityService;
import com.example.cinema.bl.sales.TicketService;
import com.example.cinema.blImpl.management.hall.HallServiceForBl;
import com.example.cinema.blImpl.management.schedule.ScheduleServiceForBl;
import com.example.cinema.blImpl.promotion.CouponServiceForBl;
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

    @Override
    @Transactional
    public ResponseVO addTicket(TicketForm ticketForm) {
        try{
            List<Ticket> tickets= new ArrayList<Ticket>();

            int userId=ticketForm.getUserId();
            int scheduleId=ticketForm.getScheduleId();
            List<SeatForm> seats= ticketForm.getSeats();
            Ticket ticket= new Ticket();
            for(SeatForm s: seats){
                ticket.setUserId(userId) ;
                ticket.setScheduleId(scheduleId);
                ticket.setColumnIndex(s.getColumnIndex());
                ticket.setRowIndex(s.getRowIndex());
                ticket.setState(0);
                tickets.add(ticket);
            }
            ticketMapper.insertTickets(tickets);
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
            List<TicketVO> ticketVOS=new ArrayList<>();
            List<Coupon> coupons=new ArrayList<>();
            Coupon coupon=couponService.getCouponById(couponId);
            //Activity activity=activityMapper.
            //activity.setCoupon(coupon);
            //activity.
            double sum=0;
            for(Integer i: id){
                tickets.add(ticketMapper.selectTicketById(i));
            }
            for(Ticket t:tickets){
                double tempAmount=0;
                int scheduleId=t.getScheduleId();
                ScheduleItem schedule=scheduleService.getScheduleItemById(scheduleId);
                t.setState(1);
                double fare=schedule.getFare();

                TicketVO ticketVO=new TicketVO();
                ticketVO.setId(t.getId());
                ticketVO.setColumnIndex(t.getColumnIndex());
                ticketVO.setRowIndex(t.getRowIndex());
                ticketVO.setScheduleId(t.getScheduleId());
                ticketVO.setState(Integer.toString(t.getState()));
                ticketVO.setTime(t.getTime());
                ticketVO.setUserId(t.getUserId());
                ticketVOS.add(ticketVO);

                if(fare>=coupon.getTargetAmount() && t.getTime().before(coupon.getEndTime()) && t.getTime().after(coupon.getStartTime())){
                    tempAmount=fare-coupon.getDiscountAmount();
                }
                else{
                    tempAmount=fare;
                }
                sum+=tempAmount;
            }
            TicketWithCouponVO ticketWithCouponVO=new TicketWithCouponVO();
            coupons.add(coupon);
            ticketWithCouponVO.setCoupons(coupons);
            ticketWithCouponVO.setTicketVOList(ticketVOS);
            ticketWithCouponVO.setTotal(sum);

            return ResponseVO.buildSuccess(ticketWithCouponVO);

        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("完成支付失败");
        }
    }

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
    }

    @Override
    @Transactional
    public ResponseVO completeByVIPCard(List<Integer> id, int couponId) {
        return null;
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

    }



}
