package com.twitter.tweathersdk.core;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

/**
 * Created by anuj on 3/2/18.
 *
 * Singleton class to get instance of OkHttpClient
 *
 * It is best practice to use a single OkHttpClient example
 * and it's in fact recommended by OkHttp
 *
 * Source: https://square.github.io/okhttp/3.x/okhttp/okhttp3/OkHttpClient.html
 */

class OkHttpFactory {
    // Singleton OkHttpClient Object
    private static OkHttpClient okHttpClient = null;

    // Timeout - Used mainly for slower connections; we don't
    // want the users to wait for a long time when in spotty
    // network conditions
    private static final long CONNECTION_TIMEOUT_IN_SECONDS = 15;
    private static final long READ_TIMEOUT_IN_SECONDS = 30;

    /**
     * Private constructor to implement a
     * Singleton
     *
     */
    private OkHttpFactory() {
        // Nothing to do
    }

    /**
     * Overridden clone method that throws an exception
     * when called - this is to ensure singleton object
     * is not cloned
     *
     * @throws CloneNotSupportedException - To support singleton
     */
    @Override
    public OkHttpFactory clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cannot clone a singleton object");
    }

    /**
     * Method to get instance of singleton OkHttpClient
     * object
     *
     * @return OkHttpClient - Singleton HTTP Client Object
     */
    static synchronized OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(OkHttpFactory.CONNECTION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(OkHttpFactory.READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false) // Do not retry on connection time out
                    .build();
        }

        return okHttpClient;
    }
}
