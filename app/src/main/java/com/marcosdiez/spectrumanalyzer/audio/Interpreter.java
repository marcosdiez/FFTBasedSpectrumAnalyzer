package com.marcosdiez.spectrumanalyzer.audio;

import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.text.AsciiAndSmallAscii;
import com.marcosdiez.spectrumanalyzer.text.SmallAsciiAndFrequencies;

/**
 * Created by Marcos on 24-May-15.
 */
public class Interpreter implements Communication.Beeper {

    private StringBuffer smallAsciiWord = new StringBuffer(10);
    private StringBuffer output = new StringBuffer(500);
    private long lastInputTimeStamp = 0;

    public synchronized String getOutput() {
        return output.toString();
    }

    public synchronized void clearOutput() {
        output.setLength(0);
    }


    private synchronized void outputAppender(char letter) {
        output.append(letter);
    }

    public void processFrequency(int frequnecy) {
        //clearBufferIfIdleForTooLong();
        if( frequnecy == Globals.max_frequency){
            clearWord();
            return;
        }

        char letterOfASmallAsciiWord = SmallAsciiAndFrequencies.toSmallAscii(frequnecy);
        smallAsciiWord.append(letterOfASmallAsciiWord);

        if (smallAsciiWord.length() == Globals.smallascii_words_per_character) {
            char theLetter;
            try {
                theLetter = AsciiAndSmallAscii.toAscii(smallAsciiWord.toString());
            } catch (IllegalArgumentException e) {
                theLetter = '*';
            }
            outputAppender(theLetter);
        }
    }

    private void clearBufferIfIdleForTooLong() {
        long now = System.currentTimeMillis();
        long delta = now - lastInputTimeStamp;
        if (delta > Globals.miliseconds_between_beeps_for_end_of_message) {
            clearWord();
        }
        lastInputTimeStamp = now;
    }

    private void clearWord() {
        smallAsciiWord.setLength(0);
    }


}