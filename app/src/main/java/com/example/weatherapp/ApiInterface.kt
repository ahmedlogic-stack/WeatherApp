package com.example.weatherapp

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
  suspend  fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): WeatherApp

}