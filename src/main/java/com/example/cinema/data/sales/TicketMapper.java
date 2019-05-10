package com.example.cinema.data.sales;

import com.example.cinema.po.Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Created by liying on 2019/4/16.
 */
@Mapper
public interface TicketMapper {

    /**
     * 将ticket插入
     * @param ticket
     * @return
     */
    int insertTicket(Ticket ticket);

    /**
     * 将ticket列表插入
     * @param tickets
     * @return
     */
    int insertTickets(List<Ticket> tickets);

    /**
     * 依据ticketId删除电影票
     * @param ticketId
     */
    void deleteTicket(int ticketId);

    /**
     * 更新电影票状态 0 未支付 1 已支付 2 已失效
     * @param ticketId
     * @param state
     */
    void updateTicketState(@Param("ticketId") int ticketId, @Param("state") int state);

    /**
     * 选择同一场次的所有电影票
     * @param scheduleId
     * @return
     */
    List<Ticket> selectTicketsBySchedule(int scheduleId);

    /**
     * 根据场次与座位选取电影票
     * 可以通过该方法锁定用户
     * @param scheduleId
     * @param columnIndex
     * @param rowIndex
     * @return
     */
    Ticket selectTicketByScheduleIdAndSeat(@Param("scheduleId") int scheduleId, @Param("column") int columnIndex, @Param("row") int rowIndex);

    /**
     * 通过电影票id选择电影票
     * @param id
     * @return
     */
    Ticket selectTicketById(int id);

    /**
     * 选择某个用户的所有电影票
     * @param userId
     * @return
     */
    List<Ticket> selectTicketByUser(int userId);

    @Scheduled(cron = "0/1 * * * * ?")
    void cleanExpiredTicket();
}

