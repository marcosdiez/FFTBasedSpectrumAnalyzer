package com.marcosdiez.spectrumanalyzer;

import android.util.Log;

/**
 * Created by Marcos on 29-Mar-15.
 */
public class CalculateStatistics {

    public static final int maxSamples = 100;
    public static final int initialNumSamples = 2;

    public static final int maxMinumumAudioVolumeWeConsider = 10;
    public static final int initialMinumumAudioVolumeWeConsider = 3;

    int numSamples = initialNumSamples;
    double minimumAudioVolumeWeConsider = initialMinumumAudioVolumeWeConsider;

    static final String TAG = "CalculateStatistics";

    int maxX;
    double maxY;
    int currentI = 0;
    int[] lastX;
    double[] lastY;
    double largestX = 0;
    double largestY = 0;


    public void setInitialMinumumAudioVolumeWeConsider(double minimumAudioVolumeWeConsider) {
        this.minimumAudioVolumeWeConsider = minimumAudioVolumeWeConsider;
    }

    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
    }

    public double getLargestX() {
        return largestX;
    }

    public double getLargestY() {
        return largestY;
    }

    //boilerplate ends here

    public CalculateStatistics() {
        lastX = new int[maxSamples + 1];
        lastY = new double[maxSamples + 1];
        currentI = 0;
        for (int i = 0; i < maxSamples; i++) {
            lastX[i] = 0;
            lastY[i] = 0;
        }
        beforeIteration();
    }

    private void beforeIteration() {
        maxX = 0;
        maxY = 0;
    }


    public void calculateStatistics(double[] toTransform) {
        beforeIteration();
        for (int i = 0; i < toTransform.length; i++) {
            double toAnalyze = toTransform[i];
            if (toAnalyze > maxY) {
                maxX = i;
                maxY = toAnalyze;
            }
        }
        afterIteration();
    }

    private void afterIteration() {
        if (maxY > minimumAudioVolumeWeConsider) {
            lastY[currentI] = maxY;
            lastX[currentI] = maxX;
        } else {
            lastY[currentI] = 0;
            lastX[currentI] = 0;
        }
        currentI = (currentI + 1) % numSamples;
        calculate();
    }

    private void calculate() {
        double localLargestY = 0;
        int localLargestX = 0;

        int n = 0;

        for (int i = 0; i < numSamples; i++) {
            double thisY = lastY[i];
            double thisX = lastX[i];

            if (thisY > 0) {
                n++;
                localLargestX += thisX;
                localLargestY += thisY;
            }

        }


        largestX = localLargestX / (double) n;
        largestY = localLargestY / (double) n;
    }


    int lastConvertedIndex = 0;
    String msg = "";

    public String createMsg() {
        double convertFactor = 4000d / (double) AudioProcessor.blockSize;
        int convertedIndex = (int) (getLargestX() * convertFactor);
        if (getLargestY() > .01) {
            if (convertedIndex != lastConvertedIndex) {
                lastConvertedIndex = convertedIndex;
                msg = "Local: " + convertedIndex + " Hz " + getLargestY();
                Log.d(TAG, msg);
            }
        }
        return msg;
    }
}
