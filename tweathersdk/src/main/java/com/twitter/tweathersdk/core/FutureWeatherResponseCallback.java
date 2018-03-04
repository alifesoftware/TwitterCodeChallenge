package com.twitter.tweathersdk.core;

import android.util.Log;

import com.google.gson.Gson;
import com.twitter.tweathersdk.model.WeatherDataPojo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by anuj on 3/3/18.
 *
 * Callback that is invoked for future weather
 * endpoint response
 *
 */

public class FutureWeatherResponseCallback implements Callback {
    // Log Tag
    private final static String TAG = FutureWeatherResponseCallback.class.getSimpleName();

    /**
     * onFailure is called when network request fails
     *
     * @param call - Request that was originally made
     * @param e - Exception
     */
    @Override
    public void onFailure(Call call, IOException e) {
        Log.e(TAG, "onFailure with exception " + e);
        TweatherSdk.getTweatherSdkInstance().futureWeatherDataRequestComplete(null);
    }

    /**
     * onResponse is called when network response is successfully
     * fetched
     *
     * @param call - Request that was originally made
     * @param response - Response from the API
     * @throws IOException
     */
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        WeatherDataPojo weatherData = null;

        try {
            if (!response.isSuccessful()) {
                Log.e(TAG, "onResponse - Response is not successful.");
                // Nothing else to do except logging. Logging is good :)
            } else {
                Log.d(TAG, "onResponse - Get WeatherDataPojo from response body");
                final String jsonResponse = response.body().string();

                Gson gson = new Gson();
                weatherData = gson.fromJson(jsonResponse, WeatherDataPojo.class);
            }
        }
        catch (Exception e) {
            Log.e(TAG, "onResponse - Exception getting response. " + e);
        }

        TweatherSdk.getTweatherSdkInstance().futureWeatherDataRequestComplete(weatherData);
    }
}
