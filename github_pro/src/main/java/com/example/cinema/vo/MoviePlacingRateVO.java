package com.example.cinema.vo;

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
    private String moviePlacingRate;

	
    public MoviePlacingRateVO(String moviePlacingRate) {
        this.moviePlacingRate = moviePlacingRate;
    }
    public MoviePlacingRateVO(MoviePlacingRate moviePlacingRate) {
        this.moviePlacingRate = moviePlacingRate.getMoviePlacingRate();
        this.movieName = moviePlacingRate.getMovieName();
    }
	
    public String getMoviePlacingRate() {
        return moviePlacingRate;
    }

    public void setMoviePlacingRate(String moviePlacingRate) {
        this.moviePlacingRate = moviePlacingRate;
    }
}

public class MoviePlacingRateVO {

    public String getMoviePlacingRate() {
        return moviePlacingRate;
    }

    public void setMoviePlacingRate(String moviePlacingRate) {
        this.moviePlacingRate = moviePlacingRate;
    }
}
