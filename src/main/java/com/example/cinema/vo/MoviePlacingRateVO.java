package com.example.cinema.vo;

import com.example.cinema.po.MoviePlacingRate;

public class MoviePlacingRateVO {
    /**
     * 上座率
     */
    private String moviePlacingRate;

    public MoviePlacingRateVO(MoviePlacingRate moviePlacingRate) {
        this.moviePlacingRate = moviePlacingRate.getMoviePlacingRate();
    }

    public String getMoviePlacingRate() {
        return moviePlacingRate;
    }

    public void setMoviePlacingRate(String moviePlacingRate) {
        this.moviePlacingRate = moviePlacingRate;
    }
}
