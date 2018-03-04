package com.twitter.challenge.presenter;

import com.twitter.tweathersdk.model.WeatherDataPojo;

/**
 * Created by anuj on 3/3/18.
 *
 * Presenter interface for MVP pattern
 *
 */
public interface WeatherPresenter {
    void start();

    void onStandardDeviationButtonClicked();

    void stop();

    WeatherDataPojo getCurrentWeather();

    Double getStandardDeviation();
}
