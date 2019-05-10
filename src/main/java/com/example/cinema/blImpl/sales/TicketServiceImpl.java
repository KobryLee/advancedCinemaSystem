package com.example.cinema.blImpl.sales;

import com.example.cinema.bl.promotion.CouponService;
import com.example.cinema.bl.sales.TicketService;
import com.example.cinema.blImpl.management.hall.HallServiceForBl;
import com.example.cinema.blImpl.management.schedule.ScheduleServiceForBl;
import com.example.cinema.data.promotion.CouponMapper;
import com.example.cinema.data.sales.TicketMapper;
import com.example.cinema.po.Coupon;
import com.example.cinema.po.Hall;
import com.example.cinema.po.ScheduleItem;
import com.example.cinema.po.Ticket;
import com.example.cinema.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liying on 2019/4/16.
 */
@Service
public class TicketServiceImpl implements TicketService {
    //Ticket是PO 用于数据库和业务逻辑层交互
    //TicketForm是VO 用于前端和业务逻辑层交互
    //问题在于怎么把TicketForm转化为Ticket
    //那我们需要用TicketForm里面的变量 抽出来 当作参数传进去
    //然后通过ticketMapper得到ticket 的返回值
    //keyProperty是Java对象的属性名！
    @Autowired
    TicketMapper ticketMapper;
    @Autowired
    ScheduleServiceForBl scheduleService;
    @Autowired
    HallServiceForBl hallService;
    @Autowired
    CouponMapper couponMapper;

    /**
     * 添加电影票
     * controller lockSear
     * @param ticketForm
     * @return
     */
    @Override
    @Transactional
    public ResponseVO addTicket(TicketForm ticketForm){
        try{
            List<Ticket> tickets = new ArrayList<Ticket>();   //应该是实现这个的
            Ticket ticket = new Ticket();

            int scheduleId = ticketForm.getScheduleId();
            int userId = ticketForm.getUserId();
            List<SeatForm> seats = ticketForm.getSeats();
            for (SeatForm s:seats) {

                int rowIndex = s.getRowIndex();
                int colIndex = s.getColumnIndex();
                ticket.setScheduleId(scheduleId);
                ticket.setUserId(userId);
                ticket.setRowIndex(rowIndex);
                ticket.setColumnIndex(colIndex);
                ticket.setState(0);
                tickets.add(ticket);
            }
            ticketMapper.insertTicket(ticket);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
        return ResponseVO.buildSuccess();

    }

    @Override
    @Transactional
    public ResponseVO completeTicket(List<Integer> id, int couponId) {

        return null;
    }


    /**
     * 用于查看座位信息
     * controller getOccupiedSeats
     * @param scheduleId
     * @return
     */
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
            return ResponseVO.buildSuccess(ticketMapper.selectTicketByUser(userId));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    @Transactional
    public ResponseVO completeByVIPCard(List<Integer> id, int couponId) {
        return null;
    }

    @Override
    public ResponseVO cancelTicket(List<Integer> id) {
        try{
            for (Integer i: id) {
                ticketMapper.deleteTicket(i);
            }
            return ResponseVO.buildSuccess();
        }catch(Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }



}
