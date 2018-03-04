package com.twitter.tweathersdk.core.events;

import com.twitter.tweathersdk.model.WeatherDataPojo;

import java.util.List;

/**
 * Created by anuj on 3/3/18.
 *
 * Event that is sent by EventBus in response
 * to Future Weather API request
 */

public class FutureWeatherEvent {
    // Future WeatherDataPojo
    private List<WeatherDataPojo> futureWeatherData;

    /**
     * Constructor
     *
     * @param weatherData - List<WeatherDataPojo>
     */
    public FutureWeatherEvent(final List<WeatherDataPojo> weatherData) {
        this.futureWeatherData = weatherData;
    }

    /**
     * Method to get POJO for future weather data
     *
     * @return List<WeatherDataPojo>
     */
    public List<WeatherDataPojo> getFutureWeatherData() {
        return this.futureWeatherData;
    }
}
