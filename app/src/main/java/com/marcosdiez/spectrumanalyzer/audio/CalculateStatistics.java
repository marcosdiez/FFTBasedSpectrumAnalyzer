package com.marcosdiez.spectrumanalyzer.audio;

import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;

/**
 * Created by Marcos on 29-Mar-15.
 */
public class CalculateStatistics {

    // X is a factor of frequency, in some wierd unity.
    // Y is the volume, in some wierd unity

    static final String TAG = "XB-CalculateStatistics";

    int maxX;
    double maxY;
    int currentI = 0;
    int[] lastX;
    double[] lastY;
    double largestX = 0;
    double largestY = 0;

    // BlockingQueue q = new ArrayBlockingQueue(100);

    public double getLargestX() {
        return largestX;
    }

    public double getLargestY() {
        return largestY;
    }

    //boilerplate ends here

    public CalculateStatistics() {
        lastX = new int[Globals.num_samples_max + 1];
        lastY = new double[Globals.num_samples_max + 1];
        currentI = 0;
        for (int i = 0; i < Globals.num_samples_max; i++) {
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
        if (maxY > Globals.minumum_audio_volume_to_be_considered) {
            lastY[currentI] = maxY;
            lastX[currentI] = maxX;
        } else {
            lastY[currentI] = 0;
            lastX[currentI] = 0;
        }
        currentI = (currentI + 1) % Globals.num_samples;
        calculate();
    }

    private void calculate() {
        double localLargestY = 0;
        int localLargestX = 0;

        int n = 0;

        for (int i = 0; i < Globals.num_samples; i++) {
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

        if (largestY > Globals.minumum_audio_volume_to_be_considered) {
            int unConvertedIndex = (int) (getLargestX() * convertFactor);
            int convertedIndex = normalizeIndex(unConvertedIndex);
            if (convertedIndex != lastConvertedIndex) {
                lastConvertedIndex = convertedIndex;
                msg = "mLocal: " + convertedIndex + "/" + unConvertedIndex + " Hz " + getLargestY() + " z:" + number_of_occurences;
                Log.d(TAG, msg);
                number_of_occurences = 0;
            }
            else{
                number_of_occurences++;
            }
        }


//        if(convertedIndex != lastConvertedIndex &&  largestY > minimumAudioVolumeWeConsider){
//            int convertedIndex = (int) (getLargestX() * convertFactor);
//            convertedIndex = normalizeIndex(convertedIndex);
//            lastLargestX = largestX;
//            msg = "Local: " + convertedIndex + " Hz " + getLargestY() + " z:" + number_of_occurences;
//            Log.d(TAG, msg);
//            // q.add(convertedIndex);
//        }
    }


    int lastConvertedIndex = 0;
    String msg = "";

//    int delta = Math.abs(Globals.max_frequency - Globals.min_frequency);
//    int frequencyIncrement = delta / Globals.words;
//    int frequency = Globals.min_frequency;
//    for (int i = 0; i <= Globals.words; i++) {
//        playSound(frequency, Globals.time_of_generated_sound);
//        frequency += frequencyIncrement;
//    }
//

    public static int normalizeIndex(int originalIndex) {
        int returnValue = 0;
        int delta = Math.abs(Globals.max_frequency - Globals.min_frequency) / Globals.words;
        int step = delta / 2;

        while (returnValue < Globals.max_frequency) {
            if(originalIndex < step){
                return returnValue;
            }

            returnValue += delta;
            step += delta;

        }
        return Globals.max_frequency;
    }



    double convertFactor = (double) Globals.frequency_limit / (double) AudioProcessor.blockSize;

    int number_of_occurences = 0;

    public String createMsg() {
        return msg;

        //int convertedIndex = (int) q.take();
//
//        int convertedIndex = (int) (getLargestX() * convertFactor);
//        convertedIndex = normalizeIndex(convertedIndex);
//        if (convertedIndex != lastConvertedIndex) {
//            lastConvertedIndex = convertedIndex;
//            msg = "Conv: " + convertedIndex + " Hz";
//        }

//        if (getLargestY() < .5) {
//            number_of_occurences++;
//            return msg;
//        }
//
//        int convertedIndex = (int) (getLargestX() * convertFactor);
//        convertedIndex = normalizeIndex(convertedIndex);
//
//        if (convertedIndex != lastConvertedIndex) {
//            lastConvertedIndex = convertedIndex;
//            msg = "Local: " + convertedIndex + " Hz " + getLargestY() + " z:" + number_of_occurences;
//            Log.d(TAG, msg);
//            number_of_occurences = 0;
//        }
//
//        return msg;
    }
}
