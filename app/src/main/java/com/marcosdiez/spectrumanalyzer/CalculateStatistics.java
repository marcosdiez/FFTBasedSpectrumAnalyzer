package com.marcosdiez.spectrumanalyzer;

/**
 * Created by Marcos on 29-Mar-15.
 */
public class CalculateStatistics {

    static final int size = 20;
    int maxX;
    double maxY;
    int currentI = 0;
    int[] lastX;
    double[] lastY;
    double minY = 3;
    double largestX = 0;
    double largestY = 0;

    public CalculateStatistics() {
        lastX = new int[size];
        lastY = new double[size];
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
