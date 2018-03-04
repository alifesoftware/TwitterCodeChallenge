package com.twitter.tweathersdk.core.events;

import com.twitter.tweathersdk.model.WeatherDataPojo;

/**
 * Created by anuj on 3/2/18.
 *
 * Event that is sent by EventBus in response
 * to Current Weather API request
 *
 */

public class CurrentWeatherEvent {
    // Current WeatherDataPojo
    private WeatherDataPojo currentWeatherData;

    /**
     * Constructor
     *
     * @param weatherData - WeatherDataPojo object
     */
    public CurrentWeatherEvent(final WeatherDataPojo weatherData) {
        this.currentWeatherData = weatherData;
    }

    /**
     * Method to get POJO for current weather data
     *
     * @return WeatherDataPojo object
     */
    public WeatherDataPojo getCurrentWeatherData() {
        return this.currentWeatherData;
    }
}
