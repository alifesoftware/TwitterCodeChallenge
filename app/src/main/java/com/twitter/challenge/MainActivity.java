package com.twitter.challenge;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.twitter.challenge.presenter.WeatherPresenter;
import com.twitter.challenge.presenter.WeatherPresenterImpl;
import com.twitter.challenge.utils.DecimalUtils;
import com.twitter.challenge.utils.TemperatureConverter;
import com.twitter.tweathersdk.model.WeatherDataPojo;


public class MainActivity extends AppCompatActivity implements WeatherView, View.OnClickListener {
    // Log Tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Presenter
    private WeatherPresenter presenter;

    // TextView for Temperature
    private TextView temperatureView;

    // TextView for Wind Information
    private TextView windInformationView;

    // ImageView for Cloud Icon
    private ImageView cloudImageView;

    // TextView for Standard Deviation
    private TextView standardDeviationView;

    // Progress Wheel
    private ProgressWheel progressWheel;

    // Bundle Extras Key for Current Weather Data and Standard Deviation
    private final static String EXTRA_KEY_CURRENT_WEATHER = "key_current_weather";
    private final static String EXTRA_KEY_STANDARD_DEVIATION = "key_standard_deviation";

    // Network State Receiver
    private final NetworkStateReceiver networkStateReceiver = new NetworkStateReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureView = (TextView) findViewById(R.id.temperature);
        windInformationView = (TextView) findViewById(R.id.windInformation);
        cloudImageView = (ImageView) findViewById(R.id.cloudImage);
        standardDeviationView = (TextView) findViewById(R.id.standardDeviation);
        standardDeviationView.setVisibility(View.INVISIBLE);
        final Button standardDeviationButton = (Button) findViewById(R.id.standardDeviationButton);
        standardDeviationButton.setOnClickListener(this);

        progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);

        // Initialize the Presenter
        if (savedInstanceState == null) {
            presenter = new WeatherPresenterImpl(this);
        }
        else {
            Double standardDeviation = null;
            if (savedInstanceState.containsKey(EXTRA_KEY_STANDARD_DEVIATION)) {
                standardDeviation = savedInstanceState.getDouble(EXTRA_KEY_STANDARD_DEVIATION);
            }

            presenter = new WeatherPresenterImpl(this,
                    ((WeatherDataPojo)(savedInstanceState.getParcelable(EXTRA_KEY_CURRENT_WEATHER))),
                    standardDeviation);
        }
    }

    /**
     * Overridden onStart - Invoke Presenter's
     * onStart
     *
     */
    @Override
    public void onStart() {
        Log.d(TAG, "onStart");

        super.onStart();
        registerNetworkStateReceiver();
        presenter.start();
    }

    /**
     * Overridden onStop - Invoke Presenter's
     * onStop
     *
     */
    @Override
    public void onStop() {
        Log.d(TAG, "onStop");

        super.onStop();
        unregisterNetworkStateReceiver();
        presenter.stop();
    }

    /**
     * onSaveInstanceState is called when activity is being
     * temporarily destroyed or before a recreate, for example
     * in case of rotation change or configuration change
     *
     * @param outState - Bundle used to save state in
     *
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");

        if (presenter.getCurrentWeather() != null) {
            Log.i(TAG, "onSaveInstanceState - Adding current weather object to bundle");
            outState.putParcelable(EXTRA_KEY_CURRENT_WEATHER, presenter.getCurrentWeather());
        }

        if (presenter.getStandardDeviation() != null) {
            Log.i(TAG, "onSaveInstanceState - Adding standard deviation to bundle");
            outState.putDouble(EXTRA_KEY_STANDARD_DEVIATION, presenter.getStandardDeviation());
        }

        super.onSaveInstanceState(outState);
    }

    /**
     * Implementation of Click Listener
     *
     * @param view - View that was clicked
     *
     */
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.standardDeviationButton:
                Log.d(TAG, "onClick - Standard Deviation button clicked");
                presenter.onStandardDeviationButtonClicked();
                break;
            default:
        }
    }

    @Override
    public void showProgress() {
        try {
            progressWheel.setVisibility(View.VISIBLE);
        }
        catch (Exception e) {
            Log.w(TAG, "showProgress - Exception " + e);
        }
    }

    @Override
    public void hideProgress() {
        try {
            progressWheel.setVisibility(View.GONE);
        }
        catch (Exception e) {
            Log.w(TAG, "hideProgress - Exception " + e);
        }
    }

    @Override
    public void showError(@StringRes int stringRes) {
        try {
            Log.d(TAG, "showError");
            Toast.makeText(this, stringRes, Toast.LENGTH_SHORT)
                    .show();
        }
        catch (Exception e) {
            Log.w(TAG, "showError - Error when showing error message. " + e);
        }
    }

    /**
     * Method to update the views that show current
     * Weather conditions
     *
     * @param weatherData - Weather Data for current conditions
     */
    @Override
    public void updateCurrentWeather(final WeatherDataPojo weatherData) {
        // Better safe then sorry, being extra cautious - Null check
        if (weatherData != null) {
            // Update the TextView that shows Temperature
            final float temperatureInCelcius = (float) (weatherData.getWeather().getTemp().doubleValue());
            Log.i(TAG, "updateCurrentWeather with temperature " + temperatureInCelcius);

            temperatureView.setText(getString(R.string.temperature,
                    temperatureInCelcius,
                    TemperatureConverter.celsiusToFahrenheit(temperatureInCelcius)));
            windInformationView.setText(getString(R.string.wind_information,
                    weatherData.getWind().getSpeed(), weatherData.getWind().getDeg()));

            if (weatherData.getClouds().getCloudiness() >= 50) {
                Log.i(TAG, "updateCurrentWeather - Set cloud icon");
                cloudImageView.setVisibility(View.VISIBLE);

                // Note: I am loading the image from drawable directly to
                // the ImageView. Since there is need for only one image
                // I am not loading image from the internet, which can be done
                // using one of many popular image loading libraries.
                //
                // Picasso and Glide are popular libraries to lazy load
                // images, and are typically used when we want to load multiple
                // images (say in a ListView or GridView or RecyclerView)
                //
                cloudImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cloud));
            }
            else {
                Log.d(TAG, "updateCurrentWeather - Hide cloud icon");
                cloudImageView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Method to update the view that shows
     * standard deviation
     *
     * @param standardDeviation - Standard deviation
     */
    @Override
    public void updateStandardDeviation(final Double standardDeviation) {
        if (standardDeviation != null) {
            Log.d(TAG, "updateStandardDeviation with value " + standardDeviation);
            standardDeviationView.setVisibility(View.VISIBLE);
            standardDeviationView.setText(String.valueOf(DecimalUtils.getDecimalDisplayString(standardDeviation)));
        }
        else {
            Log.d(TAG, "updateStandardDeviation - with null");
            standardDeviationView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Helper method to register network state change
     * broadcast receiver
     *
     */
    private void registerNetworkStateReceiver() {
        try {
            Log.d(TAG, "registerNetworkStateReceiver");
            final IntentFilter connectivityIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkStateReceiver, connectivityIntentFilter);
        }
        catch (Exception e) {
            Log.w(TAG, "registerNetworkStateReceiver - Exception when registering. " + e);
        }
    }

    /**
     * Helper method to unregister network state change
     * broadcast receiver
     *
     */
    private void unregisterNetworkStateReceiver() {
        try {
            Log.d(TAG, "unregisterNetworkStateReceiver");
            unregisterReceiver(networkStateReceiver);
        }
        catch (Exception e) {
            Log.w(TAG, "unregisterNetworkStateReceiver - Exception when unregistering. " + e);
        }
    }
}
