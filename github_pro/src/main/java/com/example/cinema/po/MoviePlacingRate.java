package com.example.cinema.po;

public class MoviePlacingRate {
	
    private String movieName;
    private Double moviePlacingRate;

    public Double getMoviePlacingRate() {
        return moviePlacingRate;
    }

    public void setMoviePlacingRate(Double d) {
        this.moviePlacingRate = d;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
}
