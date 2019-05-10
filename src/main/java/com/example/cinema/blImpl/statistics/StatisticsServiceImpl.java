package com.example.cinema.blImpl.statistics;
import com.example.cinema.bl.statistics.StatisticsService;
import com.example.cinema.blImpl.management.hall.HallServiceForBl;
import com.example.cinema.data.statistics.StatisticsMapper;
import com.example.cinema.po.*;
import com.example.cinema.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author fjj
 * @date 2019/4/16 1:34 PM
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private StatisticsMapper statisticsMapper;
    @Autowired
    private HallServiceForBl hallServiceForBl;
    @Override
    public ResponseVO getScheduleRateByDate(Date date) {
        try{
            Date requireDate = date;
            if(requireDate == null){
                requireDate = new Date();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            requireDate = simpleDateFormat.parse(simpleDateFormat.format(requireDate));

            Date nextDate = getNumDayAfterDate(requireDate, 1);
            return ResponseVO.buildSuccess(movieScheduleTimeList2MovieScheduleTimeVOList(statisticsMapper.selectMovieScheduleTimes(requireDate, nextDate)));

        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public ResponseVO getTotalBoxOffice() {
        try {
            return ResponseVO.buildSuccess(movieTotalBoxOfficeList2MovieTotalBoxOfficeVOList(statisticsMapper.selectMovieTotalBoxOffice()));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public ResponseVO getAudiencePriceSevenDays() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date today = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            Date startDate = getNumDayAfterDate(today, -6);
            List<AudiencePriceVO> audiencePriceVOList = new ArrayList<>();
            for(int i = 0; i < 7; i++){
                AudiencePriceVO audiencePriceVO = new AudiencePriceVO();
                Date date = getNumDayAfterDate(startDate, i);
                audiencePriceVO.setDate(date);
                List<AudiencePrice> audiencePriceList = statisticsMapper.selectAudiencePrice(date, getNumDayAfterDate(date, 1));
                double totalPrice = audiencePriceList.stream().mapToDouble(item -> item.getTotalPrice()).sum();
                audiencePriceVO.setPrice(Double.parseDouble(String.format("%.2f", audiencePriceList.size() == 0 ? 0 : totalPrice / audiencePriceList.size())));
                audiencePriceVOList.add(audiencePriceVO);
            }
            return ResponseVO.buildSuccess(audiencePriceVOList);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    /**
     * TODO:获取所有电影某天的上座率
     * 上座率参考公式：假设某影城设有n 个电影厅、m 个座位数，相对上座率=观众人次÷放映场次÷m÷n×100%
     * 此方法中运用到的相应的操作数据库的接口和实现请自行定义和实现，相应的结果需要自己定义一个VO类返回给前端
     * @param date
     * @return
     */
    @Override
    public ResponseVO getMoviePlacingRateByDate(Date date) {
        //要求见接口说明
        try{
            //统计座位数
            List<Hall> statisticsHall = statisticsMapper.selectAllHall();
            int totalSeat = 0;
            for (Hall h: statisticsHall
                    ) {
                int row = h.getRow();
                int col = h.getColumn();
                totalSeat += row*col;
            }

            //统计观众人次    只有已支付的人才会被统计
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date = simpleDateFormat.parse(simpleDateFormat.format(date));
            List<ScheduleAudience> scheduleAudiences = statisticsMapper.selectScheduleAudience(date);
            int totalAudience = 0;
            for(ScheduleAudience s: scheduleAudiences){
                totalAudience += s.getAudience();
            }

            //计算与转换
            double placingRate = scheduleAudiences.size()/totalAudience/totalSeat/statisticsHall.size();
            DecimalFormat df = new DecimalFormat("0.00%");
            String str_moviePlacingRate = df.format(placingRate);

            MoviePlacingRate moviePlacingRate = new MoviePlacingRate();
            moviePlacingRate.setMoviePlacingRate(str_moviePlacingRate);
            MoviePlacingRateVO moviePlacingRateVO = new MoviePlacingRateVO(moviePlacingRate);
            return ResponseVO.buildSuccess(moviePlacingRateVO);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }
    /**
     * TODO:获取最近days天内，最受欢迎的movieNum个电影(可以简单理解为最近days内票房越高的电影越受欢迎)
     * 此方法中运用到的相应的操作数据库的接口和实现请自行定义和实现，相应的结果需要自己定义一个VO类返回给前端
     * @param days
     * @param movieNum
     * @return
     */
    @Override
    public ResponseVO getPopularMovies(int days, int movieNum) {
        //要求见接口说明
        Date nowDate = new Date();
        Date lastDate = nowDate - days;
        statisticsMapper.selectPopularMovies();
        return null;
    }


    /**
     * 获得num天后的日期
     * @param oldDate
     * @param num
     * @return
     */
    Date getNumDayAfterDate(Date oldDate, int num){
        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTime(oldDate);
        calendarTime.add(Calendar.DAY_OF_YEAR, num);
        return calendarTime.getTime();
    }

    Date getNumDayBeforeDate(Date nowDate, int num){
        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTime(nowDate);
        calendarTime.add(Calendar.DAY_OF_YEAR,-num);
        return calendarTime.getTime();
    }



    private List<MovieScheduleTimeVO> movieScheduleTimeList2MovieScheduleTimeVOList(List<MovieScheduleTime> movieScheduleTimeList){
        List<MovieScheduleTimeVO> movieScheduleTimeVOList = new ArrayList<>();
        for(MovieScheduleTime movieScheduleTime : movieScheduleTimeList){
            movieScheduleTimeVOList.add(new MovieScheduleTimeVO(movieScheduleTime));
        }
        return movieScheduleTimeVOList;
    }


    private List<MovieTotalBoxOfficeVO> movieTotalBoxOfficeList2MovieTotalBoxOfficeVOList(List<MovieTotalBoxOffice> movieTotalBoxOfficeList){
        List<MovieTotalBoxOfficeVO> movieTotalBoxOfficeVOList = new ArrayList<>();
        for(MovieTotalBoxOffice movieTotalBoxOffice : movieTotalBoxOfficeList){
            movieTotalBoxOfficeVOList.add(new MovieTotalBoxOfficeVO(movieTotalBoxOffice));
        }
        return movieTotalBoxOfficeVOList;
    }
}
