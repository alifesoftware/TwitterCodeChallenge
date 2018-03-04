package com.twitter.challenge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.twitter.tweathersdk.core.TweatherSdk;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by anuj on 3/3/18.
 *
 * It is possible that user doesn't have internet
 * connection when they first launch the app. In such
 * a case, it is better to update the current weather
 * data when network comes back up
 *
 */

public class NetworkStateReceiver extends BroadcastReceiver {
    // Log Tag
    private final static String TAG = NetworkStateReceiver.class.getSimpleName();

    // Defined states for online and offline
    private final static int STATE_ONLINE = 1;
    private final static int STATE_OFFLINE = -1;

    // Current State - Online by default
    private AtomicInteger currentState = new AtomicInteger(STATE_ONLINE);

    // Wait time before we try to fetch data when network is online after offline
    private static final int WAIT_TIME_BEFORE_CALLING_API_ON_NETWORK_ONLINE_IN_SECONDS = 6 * 1000; // 6 Seconds

    /**
     * onReceive is called when network state
     * changes
     *
     * We receive multiple notifications here, including one
     * when the app is first launched. That's why I am starting with
     * default value of Online state, and then keeping a running track
     * of current state and new state. If new state is online and it is
     * different then current state (i.e. current state is offline), it
     * implies that we just got a network online event from an offline
     * state. In this case, it is good to fire a network event to get
     * current weather data
     *
     * Note; There are multiple notifications/events because a device has
     * multiple radios/network interfaces. So the above is *not* a hack to
     * prevent multiple notifications. I understand that there can be multiple
     * notifications (and the reason behind it), and I am just handling it here.
     *
     * @param context - Context
     * @param intent - Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            int newState = (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) ? STATE_ONLINE : STATE_OFFLINE;

            Log.i(TAG, "onReceive - currentState = " + currentState.get());
            Log.i(TAG, "onReceive - newState = " + newState);

            if (newState != currentState.get() &&
                    newState == STATE_ONLINE) {
                Log.i(TAG, "onReceive - Just got network connectivity");

                // This is why I like my design - there are no interfaces or callbacks involved. I can
                // fire a request to get current weather data, and because of EventBus, once
                // the response is received, weather data will be directly published and
                // Presenter will receive it. Presenter will then update the UI :)
                //
                // Note: One drawback or rather side effect of this, which actually might
                // be a good thing in real world application is that current weather data
                // will get updated every time network connection is online after offline (once
                // per offline -> online transition).
                // I am not protecting against a data refresh here, but it can be easily done
                // by caching current state and not making network request if current cache has
                // weather data. I'd rather update weather data when network is online after offline.
                //
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TweatherSdk.getTweatherSdkInstance().requestCurrentWeatherData();
                    }
                }, WAIT_TIME_BEFORE_CALLING_API_ON_NETWORK_ONLINE_IN_SECONDS);
            }
            else {
                Log.d(TAG, "onReceive - Ignore network state change as the state is same or new state if offline");
            }

            currentState.set(newState);
        }
        catch (Exception e) {
            Log.e(TAG, "onReceive - Exception processing. " + e);
        }
    }
}
