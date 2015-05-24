package com.marcosdiez.spectrumanalyzer.text;

import com.marcosdiez.spectrumanalyzer.Globals;

import java.util.HashMap;

/**
 * Created by Marcos on 24-May-15.
 */
public class AsciiAndSmallAscii {
    public static final String alphabet = "; 123456789.abcdefghijklmnopqrstuvwyxz";
    static HashMap<Character, String> letterToEncoded = null;
    static HashMap<String, Character> encodedToLetter = null;

    private static void init_if_necessary() {
        /*
            we must map our letters to sequences of words (numbers)
            this numbers can not ever repeat or else our detection algorithm breaks
            (because java is not real time.

            so for now we have this:

            Adding ; <--> 010
            Adding   <--> 012
            Adding 1 <--> 013
            Adding 2 <--> 014
            Adding 3 <--> 020
            Adding 4 <--> 021
            Adding 5 <--> 023
            Adding 6 <--> 024
            Adding 7 <--> 030
            Adding 8 <--> 031
            Adding 9 <--> 032
            Adding . <--> 034
            Adding a <--> 040
            Adding b <--> 041
            Adding c <--> 042
            Adding d <--> 043
            Adding e <--> 101
            Adding f <--> 102
            Adding g <--> 103
            Adding h <--> 104
            Adding i <--> 120
            Adding j <--> 121
            Adding k <--> 123
            Adding l <--> 124
            Adding m <--> 130
            Adding n <--> 131
            Adding o <--> 132
            Adding p <--> 134
            Adding q <--> 140
            Adding r <--> 141
            Adding s <--> 142
            Adding t <--> 143
            Adding u <--> 201
            Adding v <--> 202
            Adding w <--> 203
            Adding y <--> 204
            Adding x <--> 210
            Adding z <--> 212

         */
        if (letterToEncoded != null) {
            return;
        }
        int dictionarySize = alphabet.length();

        letterToEncoded = new HashMap<Character, String>(dictionarySize);
        encodedToLetter = new HashMap<String, Character>(dictionarySize);

        int counter = 0;
        for (int i = 0; i < Globals.words; i++) {
            for (int j = 0; j < Globals.words; j++) {
                for (int k = 0; k < Globals.words; k++) {
                    if (i != j && j != k) {
                        String encodedString = i + "" + j + "" + k;
                        Character letter = alphabet.charAt(counter);

                        System.out.println("Adding " + letter + " <--> " + encodedString);

                        letterToEncoded.put(letter, encodedString);
                        encodedToLetter.put(encodedString, letter);


                        counter++;
                        if (counter == dictionarySize) {
                            return;
                        }
                    }
                }
            }
        }
    }

    public static String toSmallAscii(char letter) {
        init_if_necessary();
        String result = letterToEncoded.get(letter);
        if (result == null) {
            throw new IllegalArgumentException("can't convert [" + letter + "]");
        }
        return result;
    }

    public static char toAscii(String fromBase) {
        init_if_necessary();
        Character result = encodedToLetter.get(fromBase);
        if (result == null) {
            throw new IllegalArgumentException("can't convert [" + fromBase + "]");
        }
        return result;
    }
}
