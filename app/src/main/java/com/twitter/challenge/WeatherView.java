package com.twitter.challenge;

import android.support.annotation.StringRes;

import com.twitter.tweathersdk.model.WeatherDataPojo;

/**
 * Created by anuj on 3/3/18.
 *
 * Interface for the View
 *
 */

public interface WeatherView {
    void showProgress();

    void hideProgress();

    void showError(@StringRes int stringRes);

    void updateCurrentWeather(final WeatherDataPojo weatherData);

    void updateStandardDeviation(final Double standardDeviation);
}
