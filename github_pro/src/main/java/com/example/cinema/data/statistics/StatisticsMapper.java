package com.example.cinema.data.statistics;

import com.example.cinema.po.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author fjj
 * @date 2019/4/16 1:43 PM
 */
@Mapper
public interface StatisticsMapper {
    /**
     * 查询date日期每部电影的排片次数
     * @param date
     * @return
     */
    List<MovieScheduleTime> selectMovieScheduleTimes(@Param("date") Date date, @Param("nextDate") Date nextDate);

    /**
     * 查询所有电影的总票房（包括已经下架的，降序排列）
     * @return
     */
    List<MovieTotalBoxOffice> selectMovieTotalBoxOffice();

    /**
     * 查询某天每个客户的购票金额
     * @param date
     * @param nextDate
     * @return
     */
    List<AudiencePrice> selectAudiencePrice(@Param("date") Date date, @Param("nextDate") Date nextDate);

    /**
     * 查询某天排片的观众人次
     * @param date
     * @return
     */
    List<ScheduleAudience> selectScheduleAudience(@Param("date") Date date);

    /**
     * 查询两个日期内最受欢迎的电影
     * @param lastDate
     * @param nowDate
     * @return
     */
    List<PopularMovie> selectPopularMovies(@Param("lastDate") Date lastDate, @Param("nowDate") Date nowDate);

    /**
     * 查询所有影厅
     * @return
     */
    List<Hall> selectAllHall();

    String selectMovieName(Integer movieId);
}
