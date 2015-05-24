package com.marcosdiez.spectrumanalyzer.text;

import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.audio.Beeper;

/**
 * Created by Marcos on 24-May-15.
 */
public class Interpreter implements Beeper {
    private static String TAG = "XB-Interpreted";
    private StringBuffer smallAsciiWord = new StringBuffer(10);
    private StringBuffer output = new StringBuffer(500);

    private String output_cache = "";

    public String getOutput() {
        return output_cache;
    }

    public synchronized void clearOutput() {
        output.setLength(0);
        output_cache = "";
    }

    private synchronized void outputAppender(char letter) {
        output.append(letter);
        output_cache = output.toString();
    }

    public void processFrequency(int frequency) {
        if (frequency == Globals.max_frequency) {
            clearWord();
            return;
        }

        char letterOfASmallAsciiWord = SmallAsciiAndFrequencies.toSmallAscii(frequency);
        smallAsciiWord.append(letterOfASmallAsciiWord);

        if (smallAsciiWord.length() == Globals.smallascii_words_per_character) {
            char theLetter;
            try {
                theLetter = AsciiAndSmallAscii.toAscii(smallAsciiWord.toString());
            } catch (IllegalArgumentException e) {
                theLetter = '*';
            }
            outputAppender(theLetter);

            Log.d(TAG, getOutput());
        }
    }

    private void clearWord() {
        smallAsciiWord.setLength(0);
    }


}