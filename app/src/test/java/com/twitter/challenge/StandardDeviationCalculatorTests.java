package com.twitter.challenge;

import com.twitter.challenge.utils.StandardDeviationCalculator;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.within;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anuj on 3/2/18.
 *
 * Basic unit tests for StandardDeviationCalculator class
 *
 */

public class StandardDeviationCalculatorTests {
    /**
     * Test case to verify that passing a null input
     * would result in IllegalArgumentException
     *
     */
    @Test (expected = IllegalArgumentException.class)
    public void testStandardDeviationForNullInput() {
        StandardDeviationCalculator.calculateStandardDeviation(null);
    }

    /**
     * Test case to verify that passing an empty input
     * would result in IllegalArgumentException
     *
     */
    @Test (expected = IllegalArgumentException.class)
    public void testStandardDeviationForEmptyInput() {
        StandardDeviationCalculator.calculateStandardDeviation(new ArrayList<Double>());
    }

    /**
     * Test case to verify that standard deviation of
     * a single value is 0
     *
     */
    @Test
    public void testStandardDeviationForOneElement() {
        final List<Double> inputList = new ArrayList<>();
        inputList.add(14.2);

        assertEquals(0.0d, StandardDeviationCalculator.calculateStandardDeviation(inputList), 0.0d);
    }

    /**
     * Test case to verify standard deviation of a list
     * with 5 positive values
     *
     * Note: Testing with 5 values mainly because of the
     * requirements of the assignment
     *
     */
    @Test
    public void testStandardDeviationForAllPositiveElements() {
        final List<Double> inputList = new ArrayList<>();
        final Offset<Double> precision = within(0.01d);

        inputList.add(56.27d);
        inputList.add(64.71d);
        inputList.add(58.54d);
        inputList.add(52.00d);
        inputList.add(67.15d);

        // Note: Expected value calculate using https://www.miniwebtool.com/sample-standard-deviation-calculator/
        final double expectedStandardDeviation = 6.18d;
        assertThat(StandardDeviationCalculator.calculateStandardDeviation(inputList)).isEqualTo(expectedStandardDeviation, precision);
    }

    /**
     * Test case to verify standard deviation of a list
     * with 5 negative values
     *
     * Note: Testing with 5 values mainly because of the
     * requirements of the assignment
     *
     */
    @Test
    public void testStandardDeviationForAllNegativeElements() {
        final List<Double> inputList = new ArrayList<>();
        final Offset<Double> precision = within(0.01d);

        inputList.add(-3.65d);
        inputList.add(-2.13d);
        inputList.add(-0.76d);
        inputList.add(-3.61d);
        inputList.add(-1.95d);

        // Note: Expected value calculate using https://www.miniwebtool.com/sample-standard-deviation-calculator/
        final double expectedStandardDeviation = 1.22d;
        assertThat(StandardDeviationCalculator.calculateStandardDeviation(inputList)).isEqualTo(expectedStandardDeviation, precision);
    }

    /**
     * Test case to verify standard deviation of a list
     * with a mix of positive and negative values
     *
     * Note: Testing with 5 values mainly because of the
     * requirements of the assignment
     *
     */
    @Test
    public void testStandardDeviationForMixedElements() {
        final List<Double> inputList = new ArrayList<>();
        final Offset<Double> precision = within(0.01d);

        inputList.add(5.39d);
        inputList.add(-2.56d);
        inputList.add(-7.54d);
        inputList.add(6.00d);
        inputList.add(3.31d);

        // Note: Expected value calculate using https://www.miniwebtool.com/sample-standard-deviation-calculator/
        final double expectedStandardDeviation = 5.81;
        assertThat(StandardDeviationCalculator.calculateStandardDeviation(inputList)).isEqualTo(expectedStandardDeviation, precision);
    }
}
