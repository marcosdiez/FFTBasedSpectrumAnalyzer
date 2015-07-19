package com.marcosdiez.spectrumanalyzer.audio;

import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.audio.Listener.AudioProcessor;
import com.marcosdiez.spectrumanalyzer.text.SmallAsciiAndFrequencies;
import com.marcosdiez.spectrumanalyzer.util.SaveToDatabase;

/**
 * Created by Marcos on 29-Mar-15.
 */
public class CalculateStatistics {

    // X is a factor of frequency, in some wierd unity.
    // Y is the volume, in some wierd unity

    static final String TAG = "XB-CalculateStatistics";
    final int convertFactor = (int) ((double) Globals.frequency_limit / (double) AudioProcessor.blockSize);
    int maxX = 0;
    double maxY = 0;
    // BlockingQueue q = new ArrayBlockingQueue(100);


    //boilerplate ends here
    SaveToDatabase saveToDatabase = new SaveToDatabase();
    int secondLastConvertedIndex = 0;
    int lastNumberOfOccurrences = 0;
    int lastConvertedIndex = 0;
    String msg = "";
    int number_of_occurrences = 0;
    public CalculateStatistics() {
        beforeIteration();
    }

    public static int normalizeIndex(int originalIndex) {
        int delta = Math.abs(Globals.max_frequency - Globals.min_frequency) / Globals.words;

        int lowerLimit = Globals.min_frequency - delta / 2;
        int upperLimit = lowerLimit + delta;


        if (originalIndex < lowerLimit) {
            return 0;
        }

        int returnValue = Globals.min_frequency;

        while (returnValue <= Globals.max_frequency) {
            if (originalIndex < upperLimit) {
                return returnValue;
            }

            returnValue += delta;
            upperLimit += delta;

        }
        return Globals.max_frequency;
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
            int unConvertedIndex = (maxX * convertFactor);
            int convertedIndex = normalizeIndex(unConvertedIndex);
            if (convertedIndex != lastConvertedIndex) {
                secondLastConvertedIndex = lastConvertedIndex;
                lastConvertedIndex = convertedIndex;
                lastNumberOfOccurrences = number_of_occurrences;
                number_of_occurrences = 0;
                msg = "XX: " + convertedIndex + "/" + unConvertedIndex + " Hz [" + SmallAsciiAndFrequencies.toSmallAsciiNoException(convertedIndex) + "], volume:" + ((int) maxY) +
                        ", last freq:" +
                        secondLastConvertedIndex + " Hz " + lastNumberOfOccurrences + " times";
                msg = "MA: " + unConvertedIndex + " Hz";
                Log.d(TAG, msg);
            } else {
                number_of_occurrences++;
                if (number_of_occurrences == Globals.num_samples && convertedIndex != 0) {
                    Globals.interpreter.processFrequency(convertedIndex);

//                        Globals.interpreter.
//                    saveToDatabase.sendAudioFrequency(convertedIndex);
//                    Globals.toastMsg = Globals.num_samples + " samples! Sending Signal of " + convertedIndex + " Hz.";
                }

            }
        }
    }

    public String createMsg() {
        return msg;
    }
}
