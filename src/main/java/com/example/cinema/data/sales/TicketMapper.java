package com.example.cinema.data.sales;

import com.example.cinema.po.RefundStrategy;
import com.example.cinema.po.Ticket;
import com.example.cinema.po.VIPCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Created by liying on 2019/4/16.
 */
@Mapper
public interface TicketMapper {

    int insertTicket(Ticket ticket);

    int insertTickets(List<Ticket> tickets);

    void deleteTicket(int ticketId);

    void updateTicketState(@Param("ticketId") int ticketId, @Param("state") int state);

    void updatePaymentMode(@Param("ticketId") int ticketId, @Param("paymentMode") int paymentMode);

    void updateTicketCoupon(@Param("ticketId") int ticketId, @Param("couponId") int couponId);

    List<Ticket> selectTicketsBySchedule(int scheduleId);

    Ticket selectTicketByScheduleIdAndSeat(@Param("scheduleId") int scheduleId, @Param("column") int columnIndex, @Param("row") int rowIndex);

    Ticket selectTicketById(int id);

    List<Ticket> selectTicketByUser(@Param("userId") int userId);

    @Scheduled(cron = "0/1 * * * * ?")
    void cleanExpiredTicket();
    
	void addCoupon(int id, int userId);
	
	void VIPPay(int userId, double toPay);

	void VIPRefund(int userId, double refund);

	List<RefundStrategy> getRefundStrategies(int isVip);

	List<VIPCard> isVip(int userId);

}

