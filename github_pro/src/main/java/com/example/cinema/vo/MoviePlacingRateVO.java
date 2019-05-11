package com.example.cinema.vo;

import com.example.cinema.po.MoviePlacingRate;

/**
 * 上座率
 * @author zbc
 *
 */
public class MoviePlacingRateVO {
	
	
    /**
     * 电影名称
     */
    private String movieName;
    /**
     * 上座率
     */
    private Double moviePlacingRate;

	
    public MoviePlacingRateVO(Double moviePlacingRate) {
        this.moviePlacingRate = moviePlacingRate;
    }
    public MoviePlacingRateVO(MoviePlacingRate moviePlacingRate) {
        this.moviePlacingRate = moviePlacingRate.getMoviePlacingRate();
        this.movieName = moviePlacingRate.getMovieName();
    }
	
    public String getMovieName() {
		return movieName;
	}
    
	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}
	
    public Double getMoviePlacingRate() {
        return moviePlacingRate;
    }

    public void setMoviePlacingRate(Double moviePlacingRate) {
        this.moviePlacingRate = moviePlacingRate;
    }
	
}

