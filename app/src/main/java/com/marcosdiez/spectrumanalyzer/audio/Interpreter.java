package com.marcosdiez.spectrumanalyzer.audio;

import com.marcosdiez.spectrumanalyzer.Globals;

/**
 * Created by Marcos on 24-May-15.
 */
public class Interpreter implements Communication.Beeper {

    private StringBuffer word = new StringBuffer(10);
    private StringBuffer output = new StringBuffer(500);
    private long lastInputTimeStamp = 0;

    public synchronized String getOutput() {
        return output.toString();
    }

    private synchronized void outputAppender(char letter) {
        output.append(letter);
    }

    public void beepChar(char c) {
        long now = System.currentTimeMillis();
        long delta = now - lastInputTimeStamp;
        if (delta > Globals.miliseconds_between_beeps_for_end_of_message) {
            clearWord();
        }
        lastInputTimeStamp = now;

        word.append(c);
        if (word.length() == Globals.words_per_character) {
            char theLetter = Communication.toLetter(word.toString());
            outputAppender(theLetter);
            clearWord();
        }
    }

    private void clearWord() {
        word.setLength(0);
    }

//    public void clearAll() {
//        clearWord();
//        output.setLength(0);
//    }

    public void beepWordSeparator() {
    }
}