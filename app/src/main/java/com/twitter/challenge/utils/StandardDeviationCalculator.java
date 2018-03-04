package com.twitter.challenge.utils;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by anuj on 3/2/18.
 *
 * Class that has method to calculate standard
 * deviation.
 *
 * Note: I don't think standard deviation calculation by itself
 * merits a class for just one feature/function. Standard deviation
 * can actually be a method in a Utility class (maybe a class called
 * MathUtils).
 * But since there is an existing class for TemperatureConverter,
 * I decided to have a class for Standard Deviation because it is
 * better follow the code style and organization based on existing
 * source code unless there is a compelling reason to refactor the
 * existing code.
 *
 */

public class StandardDeviationCalculator {
    /**
     * This method calculates and returns standard deviation
     * for a given list of float values.
     *
     * By definition, Standard Deviation of a single number is
     * 0 as there are no other sample points
     *
     * @param values - List of values
     * @return Standard Deviation
     * @throws IllegalArgumentException - If the input list is empty or null
     */
    public static double calculateStandardDeviation(@NonNull final List<Double> values) throws IllegalArgumentException {
        if (values == null || values.size() == 0) {
            throw new IllegalArgumentException("Invalid input size. Please make sure input list is valid and has at least one value");
        }

        if (values.size() == 1) {
            return 0.0d;
        }

        // Based on the formula provided for Standard Deviation, the steps involved
        // in calculation are:
        //
        // 1. Calculate average or mean value for input values
        // 2. For each value in input list, calculate square of (value[i] - mean), and
        // keep a running summation of each result
        // 3. Divide the summation result by N - 1, where N is the size of input list
        // 4. Standard deviation is the square root of result from #3
        double sum = 0.0d;
        for (double value : values) {
            sum += value;
        }

        final double mean = sum / values.size();
        sum = 0.0d; // Reset the sum to calculate summation

        for (double value: values) {
            sum += Math.pow(value - mean, 2);
        }

        return Math.sqrt(sum / (values.size() - 1));
    }
}
