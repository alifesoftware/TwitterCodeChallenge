package com.twitter.tweathersdk.core;

import android.util.Log;

import com.twitter.tweathersdk.core.events.CurrentWeatherEvent;
import com.twitter.tweathersdk.core.events.FutureWeatherEvent;
import com.twitter.tweathersdk.model.WeatherDataPojo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by anuj on 3/2/18.
 *
 * This class implements the SDK methods used by
 * an app using this SDK.
 *
 * Note: Design of this SDK is pretty simple and in accordance with the
 * requirements given in the assignment. I didn't go overboard
 * to design it with more flexibility.
 *
 * Example of a flexible design: There can be an interface with the required
 * SDK methods, and we can use multiple weather sources, where each weather
 * source will be a concrete class implementing that interface. That way, we the
 * user of this SDK can select which weather source they want.
 * Also, data model for each source will also implement a common interface, while
 * having different implementation based on the JSON response.
 *
 * ^ Just a thought ^
 *
 * Note 2: This SDK is based on Pub-Sub or EventBus. That means there
 * are no callbacks between the app and SDK. All results are delivered
 * via EventBus. The reason for this is if callbacks are used, then
 * callback passed to the SDK may become null in case Activity in the app
 * is recreated by rotation or configuration changes.
 */

public class TweatherSdk {
    // Log Tag
    private final static String TAG = TweatherSdk.class.getSimpleName();

    // Singleton Object
    private static TweatherSdk tweatherSdkInstance = null;

    // Base URL
    private final static String BASE_URL = "http://twitter-code-challenge.s3-website-us-east-1.amazonaws.com/";

    // Current Weather Endpoint
    private final static String CURRENT_WEATHER_ENDPOINT = "current.json";

    // List of future weather data Requests
    private static List<Request> futureWeatherRequests;
    private AtomicInteger numberOfFutureWeatherRequests;
    private AtomicInteger numberOfFutureWeatherResponse;

    // Static initializer for future weather data request list
    static {
        futureWeatherRequests = new ArrayList<>();

        // Create requests for future weather data endpoints
        Request oneDayWeatherRequest = new Request.Builder()
                .url(TweatherSdk.BASE_URL + "future_1.json")
                .build();
        Request twoDayWeatherRequest = new Request.Builder()
                .url(TweatherSdk.BASE_URL + "future_2.json")
                .build();
        Request threeDayWeatherRequest = new Request.Builder()
                .url(TweatherSdk.BASE_URL + "future_3.json")
                .build();
        Request fourDayWeatherRequest = new Request.Builder()
                .url(TweatherSdk.BASE_URL + "future_4.json")
                .build();
        Request fiveDayWeatherRequest = new Request.Builder()
                .url(TweatherSdk.BASE_URL + "future_5.json")
                .build();

        futureWeatherRequests.add(oneDayWeatherRequest);
        futureWeatherRequests.add(twoDayWeatherRequest);
        futureWeatherRequests.add(threeDayWeatherRequest);
        futureWeatherRequests.add(fourDayWeatherRequest);
        futureWeatherRequests.add(fiveDayWeatherRequest);
    }

    // Prevent multiple requests for the same endpoint
    // because weather data doesn't change over short periods of time.
    // This will help avoid unnecessary network calls
    private AtomicBoolean currentWeatherRequestInProgress = new AtomicBoolean(false);
    private AtomicBoolean futureWeatherRequestInProgress = new AtomicBoolean(false);

    // Response List for future weather data
    //
    // Note: For the requirements given, it doesn't matter
    // which order the response objects are in. We just need
    // to access all response objects
    //
    // This will be initialized as a synchronized List for thread
    // safety
    private List<WeatherDataPojo> futureResponseList;

    /**
     * Private constructor for Singleton
     *
     */
    private TweatherSdk() {
        // Nothing to do
    }

    /**
     * Overridden clone method to prevent caller from
     * cloning a singleton
     *
     * @throws CloneNotSupportedException - Exception is thrown to prevent clone of singleton
     */
    @Override
    public TweatherSdk clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cannot clone a singleton class");
    }

    /**
     * Method to get singleton instance of TweatherSdk
     *
     * @return Instance of TweatherSdk
     */
    public static synchronized TweatherSdk getTweatherSdkInstance() {
        if (tweatherSdkInstance == null) {
            Log.i(TAG, "getTweatherSdkInstance - Creating new instance");
            tweatherSdkInstance = new TweatherSdk();
            // Better to use a Synchronized List
            tweatherSdkInstance.futureResponseList =
                    Collections.synchronizedList(new ArrayList<WeatherDataPojo>());
            tweatherSdkInstance.numberOfFutureWeatherRequests = new AtomicInteger(0);
            tweatherSdkInstance.numberOfFutureWeatherResponse = new AtomicInteger(0);
        }

        return tweatherSdkInstance;
    }

    /**
     * SDK Method to request current weather data
     *
     * @return true if the request is successfully made, false otherwise
     *
     */
    public boolean requestCurrentWeatherData() {
        Log.d(TAG, "requestCurrentWeatherData called");

        if (currentWeatherRequestInProgress.get()) {
            Log.w(TAG, "requestCurrentWeatherData - Another request is already in progress. Abandon this request");
            return false;
        }

        // Set the flag
        currentWeatherRequestInProgress.set(true);

        final OkHttpClient httpClient = OkHttpFactory.getOkHttpClient();
        final String url = TweatherSdk.BASE_URL + TweatherSdk.CURRENT_WEATHER_ENDPOINT;
        final Request currentWeatherRequest = new Request.Builder()
                .url(url)
                .build();

        Log.d(TAG, "requestCurrentWeatherData - Getting current weather from: " + url);

        // Make a asynchronous request using OkHttpClient. Since async
        // API is used, there is no need to worry about whether requestCurrentWeatherData
        // method is called on main thread
        CurrentWeatherResponseCallback callback = new CurrentWeatherResponseCallback();
        httpClient.newCall(currentWeatherRequest).enqueue(callback);

        // Request accepted
        return true;
    }

    /**
     * This method is called by the network response
     * callback when request for current weather request
     * is complete
     *
     * @param weatherData - WeatherDataPojo object if request is
     *                    successful, null otherwise
     */
    void currentWeatherDataRequestComplete(final WeatherDataPojo weatherData) {
        Log.d(TAG, "currentWeatherDataRequestComplete called");

        // Publish the Event
        EventBus.getDefault().post(new CurrentWeatherEvent(weatherData));

        // Reset request progress
        currentWeatherRequestInProgress.set(false);
    }

    /**
     * SDK Method to request future weather data.
     *
     * Note: Twitter Weather API only supports future data
     * for up to 5 days. This method will fire 5 API requests
     * to get weather data for next 5 days
     *
     * Firing parallel requests to one callback works well here
     * because we do not need to know which response is for which
     * object, as we only need it to calculate StandardDeviation
     * in the code.
     *
     * @return true if the request is successfully made, false otherwise
     */
    public boolean requestFutureWeatherData() {
        Log.d(TAG, "requestFutureWeatherData called");

        if (futureWeatherRequestInProgress.get()) {
            Log.w(TAG, "requestFutureWeatherData - Another request is already in progress. Abandon this request");
            return false;
        }

        // Set the flag
        futureWeatherRequestInProgress.set(true);

        // Clear the future response collection before starting a new set of future weather
        // data requests
        futureResponseList.clear();

        // Clear request and response counters
        numberOfFutureWeatherRequests.set(0);
        numberOfFutureWeatherResponse.set(0);

        final OkHttpClient httpClient = OkHttpFactory.getOkHttpClient();
        FutureWeatherResponseCallback callback = new FutureWeatherResponseCallback();

        // Enqueue the requests for future data weather
        for (Request request : futureWeatherRequests) {
            numberOfFutureWeatherRequests.incrementAndGet();
            httpClient.newCall(request).enqueue(callback);
        }

        return true;
    }

    /**
     * This method is called by the network response
     * callback when request for future weather request
     * is complete
     *
     * @param weatherData - WeatherDataPojo object if request is
     *                    successful, null otherwise
     */
    void futureWeatherDataRequestComplete(final WeatherDataPojo weatherData) {
        Log.d(TAG, "futureWeatherDataRequestComplete called");

        // Increment the response counter
        numberOfFutureWeatherResponse.incrementAndGet();

        // Add the response to the collection
        futureResponseList.add(weatherData);

        // Reset request progress and fire complete event
        // when all responses have been received
        if (numberOfFutureWeatherRequests.get() == numberOfFutureWeatherResponse.get()) {
            Log.i(TAG, "futureWeatherDataRequestComplete - All requests have been completed");

            // Publish the event
            EventBus.getDefault().post(new FutureWeatherEvent(futureResponseList));

            // Reset the progress flag
            futureWeatherRequestInProgress.set(false);
        }
    }
}
