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

    int maxX = 0;
    double maxY = 0;

    // BlockingQueue q = new ArrayBlockingQueue(100);


    //boilerplate ends here

    public CalculateStatistics() {
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
            int unConvertedIndex = (int) (maxX * convertFactor);
            int convertedIndex = normalizeIndex(unConvertedIndex);
            if (convertedIndex != lastConvertedIndex) {
                lastConvertedIndex = convertedIndex;
                msg = "mLocal: " + convertedIndex + "/" + unConvertedIndex + " Hz " + maxY + " last_z:" + number_of_occurrences;
                Log.d(TAG, msg);
                number_of_occurrences = 0;
            } else {
                number_of_occurrences++;
            }
        }


//        if(convertedIndex != lastConvertedIndex &&  largestY > minimumAudioVolumeWeConsider){
//            int convertedIndex = (int) (getLargestX() * convertFactor);
//            convertedIndex = normalizeIndex(convertedIndex);
//            lastLargestX = largestX;
//            msg = "Local: " + convertedIndex + " Hz " + getLargestY() + " z:" + number_of_occurrences;
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
            if (originalIndex < step) {
                return returnValue;
            }

            returnValue += delta;
            step += delta;

        }
        return Globals.max_frequency;
    }


    final double convertFactor = (double) Globals.frequency_limit / (double) AudioProcessor.blockSize;

    int number_of_occurrences = 0;

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
//            number_of_occurrences++;
//            return msg;
//        }
//
//        int convertedIndex = (int) (getLargestX() * convertFactor);
//        convertedIndex = normalizeIndex(convertedIndex);
//
//        if (convertedIndex != lastConvertedIndex) {
//            lastConvertedIndex = convertedIndex;
//            msg = "Local: " + convertedIndex + " Hz " + getLargestY() + " z:" + number_of_occurrences;
//            Log.d(TAG, msg);
//            number_of_occurrences = 0;
//        }
//
//        return msg;
    }
}
