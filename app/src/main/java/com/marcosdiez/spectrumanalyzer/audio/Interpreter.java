package com.marcosdiez.spectrumanalyzer.audio;

import com.marcosdiez.spectrumanalyzer.Globals;

/**
 * Created by Marcos on 24-May-15.
 */
public class Interpreter implements Communication.Beeper {

    StringBuffer word = new StringBuffer(10);
    StringBuffer output = new StringBuffer(500);
    long lastInputTimeStamp = 0;

    public String getOutput() {
        return output.toString();
    }

    public void beepChar(char c) {
        long now = System.currentTimeMillis();
        long delta = now - lastInputTimeStamp;
        if(delta > Globals.miliseconds_between_for_end_of_message){
            clearWord();
        }
        word.append(c);
        lastInputTimeStamp=now;
    }

    public void clearWord(){
        word.setLength(0);
    }

    public void clearAll(){
        clearWord();
        output.setLength(0);
    }

    public void beepWordSeparator() {
        output.append(Communication.toLetter(word.toString()));
        clearWord();
    }
}