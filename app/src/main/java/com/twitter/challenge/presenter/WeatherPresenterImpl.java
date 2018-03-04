package com.twitter.challenge.presenter;

import android.util.Log;

import com.twitter.challenge.R;
import com.twitter.challenge.utils.StandardDeviationCalculator;
import com.twitter.challenge.WeatherView;
import com.twitter.tweathersdk.core.TweatherSdk;
import com.twitter.tweathersdk.core.events.CurrentWeatherEvent;
import com.twitter.tweathersdk.core.events.FutureWeatherEvent;
import com.twitter.tweathersdk.model.WeatherDataPojo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anuj on 3/3/18.
 *
 * Implementation of WeatherPresenter interface
 * for MVP pattern
 *
 */

public class WeatherPresenterImpl implements WeatherPresenter {
    // Log Tag
    private final static String TAG = WeatherPresenterImpl.class.getSimpleName();

    // WeatherView for the Presenter
    private WeatherView weatherView;

    // Current Weather
    private WeatherDataPojo currentWeather = null;

    // Standard Deviation
    private Double standardDeviation = null;

    /**
     * Constructor for Weather Presenter
     * @param weatherView - Weather View
     *
     */
    public WeatherPresenterImpl(final WeatherView weatherView) {
        Log.d(TAG, "Constructor with WeatherView called");
        this.weatherView = weatherView;
    }

    /**
     * Constructor for Weather Presenter with
     * temperature and standard deviation
     *
     * Ideally I should be using a Builder Pattern since there
     * are quite a few values that are being set. I can use
     * Lombok to create a Builder using annotations, but leaving
     * it as it is now with a multi-arg constructor
     *
     * @param weatherView - Weather View
     * @param currentWeather - Current WeatherDataPojo
     * @param standardDeviation - Standard Deviation for future weather
     */
    public WeatherPresenterImpl(final WeatherView weatherView,
                                final WeatherDataPojo currentWeather,
                                final Double standardDeviation) {
        Log.d(TAG, "Constructor with multiple args called");

        this.weatherView = weatherView;
        this.currentWeather = currentWeather;
        this.standardDeviation = standardDeviation;
    }

    /**
     * Implementation of Presenter start
     *
     * Subscribe to the EventBus
     *
     */
    @Override
    public void start() {
        Log.d(TAG, "start called - Register EventBus");
        EventBus.getDefault().register(this);

        // If we do not have data for temperature yet,
        // make a request
        if (currentWeather == null) {
            Log.i(TAG, "start - Current weather is null, request current weather from SDK");
            TweatherSdk.getTweatherSdkInstance().requestCurrentWeatherData();
        }
        else if (weatherView != null) {
            Log.i(TAG, "start - Current weather is not null, update the UI with weather data");
            weatherView.updateCurrentWeather(currentWeather);

            if (standardDeviation != null) {
                Log.i(TAG, "start - Standard deviation is not null, update the UI with standard deviation");
                weatherView.updateStandardDeviation(standardDeviation);
            }
        }
    }

    /**
     * Implementation of Presenter onStandardDeviationButtonClicked
     *
     */
    @Override
    public void onStandardDeviationButtonClicked() {
        Log.d(TAG, "onStandardDeviationButtonClicked");

        // Since the requirements says there is no need to refresh
        // the data when app is running, if we have already have
        // standard deviation calculated, we will not make a new
        // network request, and directly update the UI
        if (standardDeviation != null &&
                weatherView != null) {
            Log.i(TAG, "onStandardDeviationButtonClicked - Standard deviation is cached, update the View");
            weatherView.updateStandardDeviation(standardDeviation);
        }
        else {
            Log.i(TAG, "onStandardDeviationButtonClicked - Request future temperature data from SDK");

            // Request data from API using SDK
            boolean requestSuccess = TweatherSdk.getTweatherSdkInstance().requestFutureWeatherData();

            if (weatherView != null &&
                    requestSuccess) {
                weatherView.showProgress();
            }
        }
    }

    /**
     * Implementation of Presenter stop
     *
     * Un-subscribe to the EventBus
     */
    @Override
    public void stop() {
        Log.d(TAG, "stop called - Unregister EventBus");
        EventBus.getDefault().unregister(this);
    }

    /**
     * Implementation of Presenter getCurrentWeather
     *
     * @return Current weather POJO (WeatherDataPojo)
     */
    @Override
    public WeatherDataPojo getCurrentWeather() {
        return currentWeather;
    }

    /**
     * Implementation of Presenter getStandardDeviation
     *
     * @return Standard Deviation value
     */
    @Override
    public Double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * Method that is invoked when EventBus publishes
     * an event in response to one of the requests
     *
     * @param event - Event that is published
     *
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object event) {
        Log.d(TAG, "onMessageEvent called for class " + event.getClass().getSimpleName());

        if (event instanceof CurrentWeatherEvent) {
            WeatherDataPojo newWeatherData = ((CurrentWeatherEvent) event).getCurrentWeatherData();

            if (newWeatherData != null &&
                    newWeatherData.getWeather() != null &&
                    newWeatherData.getWeather().getTemp() != null) {
                this.currentWeather = newWeatherData;
                if (weatherView != null) {
                    Log.d(TAG, "onMessageEvent - Update UI with current weather information");
                    weatherView.updateCurrentWeather(currentWeather);
                } else {
                    Log.w(TAG, "onMessageEvent - Cannot update UI with current weather information");
                }
            }
        }
        else if (event instanceof FutureWeatherEvent) {
            if (weatherView != null) {
                weatherView.hideProgress();

                FutureWeatherEvent futureWeatherEvent = (FutureWeatherEvent) event;
                List<WeatherDataPojo> futureWeatherList = futureWeatherEvent.getFutureWeatherData();

                if (futureWeatherList != null &&
                        futureWeatherList.size() == 5) {
                    final List<Double> futureTemperatureList = new ArrayList<>();

                    for (WeatherDataPojo weatherData : futureWeatherList) {
                        // Show error and return in case there is something wrong with the data
                        if (weatherData == null ||
                                weatherData.getWeather() == null ||
                                weatherData.getWeather().getTemp() == null) {
                            Log.w(TAG, "onMessageEvent - Future weather data has one item null or invalid, cannot find standard deviation");

                            weatherView.showError(R.string.standard_deviation_error);
                            return;
                        }

                        // Add to the list
                        futureTemperatureList.add(weatherData.getWeather().getTemp());
                    }

                    // Calculate standard deviation
                    Log.d(TAG, "onMessageEvent - Calculate standard deviation and update the UI");
                    standardDeviation = StandardDeviationCalculator.calculateStandardDeviation(futureTemperatureList);
                    weatherView.updateStandardDeviation(standardDeviation);
                }
                else {
                    Log.w(TAG, "onMessageEvent - Did not get complete future weather data, cannot find standard deviation");

                    // Show an error using Toast
                    weatherView.showError(R.string.standard_deviation_error);
                }
            }
            else {
                Log.w(TAG, "onMessageEvent - Cannot update UI with standard deviation");
            }
        }
    }
}
