package com.twitter.challenge;

import java.text.DecimalFormat;

/**
 * Created by anuj on 3/3/18.
 *
 * Utility class to better format double and float values
 * with too many digits after the decimal
 */

class DecimalUtils {
    /**
     * Helper method to reduce the number of decimals
     * in a double
     *
     * @param value - double value to reduce decimals
     * @return Display String with less precision
     */
    static String getDecimalDisplayString(final double value) {
        final DecimalFormat decimalFormatter = new DecimalFormat("0.0000");
        return decimalFormatter.format(value);
    }

    /**
     * Helper method to reduce the number of decimals
     * in a double
     *
     * @param value - float value to reduce decimals
     * @return Display String with less precision
     */
    static String getDecimalDisplayString(final float value) {
        final DecimalFormat decimalFormatter = new DecimalFormat("0.0000");
        return decimalFormatter.format(value);
    }
}
