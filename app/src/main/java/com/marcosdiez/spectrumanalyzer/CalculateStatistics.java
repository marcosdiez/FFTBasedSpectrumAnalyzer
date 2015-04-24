package com.marcosdiez.spectrumanalyzer;

import android.util.Log;

/**
 * Created by Marcos on 29-Mar-15.
 */
public class CalculateStatistics {

    public static final int maxSamples = 100;
    public static final int initialNumSamples = 2;

//    public static final int

    int size = initialNumSamples;
    double minY = 3;

    static final String TAG = "CalculateStatistics";

    int maxX;
    double maxY;
    int currentI = 0;
    int[] lastX;
    double[] lastY;
    double largestX = 0;
    double largestY = 0;


    void setSize(int size){
        this.size=size;
    }

    public CalculateStatistics() {
        lastX = new int[maxSamples +1 ];
        lastY = new double[maxSamples +1 ];
        currentI = 0;
        for (int i = 0; i < size; i++) {
            lastX[i] = 0;
            lastY[i] = 0;
        }

        beforeIteration();
    }

    public double getLargestX() {
        return largestX;
    }

    public double getLargestY() {
        return largestY;
    }

    public void calculateStatistics(double[] toTransform) {
        beforeIteration();
        for (int i = 0; i < toTransform.length; i++) {
            double toAnalyze = toTransform[i];
            analyzeElement(i, toAnalyze);
        }
        afterIteration();
    }


    public void beforeIteration() {
        maxX = 0;
        maxY = 0;
    }

    public void analyzeElement(int x, double y) {
        if (y > maxY) {
            maxX = x;
            maxY = y;
        }
    }

    public void afterIteration() {
        if (maxY > minY) {
            lastY[currentI] = maxY;
            lastX[currentI] = maxX;
        } else {
            lastY[currentI] = 0;
            lastX[currentI] = 0;
        }
        currentI = (currentI + 1) % size;
        calculate();
    }

    String msg = "";
    public String createMsg() {
        double convertFactor = 4000d / (double) AudioProcessor.blockSize;
        int convertedIndex = (int) (getLargestX() * convertFactor);
        if (getLargestY() > .01) {
            msg = "Local: " + convertedIndex + " Hz " + getLargestY();
            Log.d(TAG, msg);
        }
        return msg;
    }


    public void calculate() {
        double localLargestY = 0;
        int localLargestX = 0;

        int n = 0;

        for (int i = 0; i < size; i++) {
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
}
