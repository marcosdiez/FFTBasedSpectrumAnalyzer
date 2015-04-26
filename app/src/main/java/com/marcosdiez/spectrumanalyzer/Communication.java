package com.marcosdiez.spectrumanalyzer;

import java.util.HashMap;

/**
 * Created by Marcos on 29-Apr-15.
 */
public class Communication {
    public static final String alphabet = "; 123456789.abcdefghijklmnopqrstuvwyxz";
    public static int numWords = 5;  // new base size

    static HashMap<Character, String> letterToEncoded = null;
    static HashMap<String, Character> encodedToLetter = null;

    public static void init() {
        /*
            we must map our letters to sequences of words (numbers)
            this numbers can not ever repeat.
         */
        if (letterToEncoded != null) {
            return;
        }
        int dictionarySize = alphabet.length();

        letterToEncoded = new HashMap<Character, String>(dictionarySize);
        encodedToLetter = new HashMap<String, Character>(dictionarySize);

        int counter = 0;
        for (int i = 0; i < numWords; i++) {
            for (int j = 0; j < numWords; j++) {
                for (int k = 0; k < numWords; k++) {
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

    public static String fixString(String input) {
        return input.replace(",", ".").toLowerCase();
    }

    public static String toNewBase(char letter) {
        init();
        return letterToEncoded.get(letter);
    }

    public static char toLetter(String fromBase) {
        init();
        return encodedToLetter.get(fromBase);
    }

    public static void player(String sentence, Beeper b) {
        int size = sentence.length();
        for (int i = 0; i < size; i++) {
            char c = sentence.charAt(i);
            String newBase = toNewBase(c);
            playerHelper(newBase, b);
        }
    }

    private static void playerHelper(String baseNumber, Beeper b) {
        int size = baseNumber.length();
        for (int i = 0; i < size; i++) {
            char c = baseNumber.charAt(i);
            b.beepChar(c);
        }
        b.beepWordSeparator();
    }

    public interface Beeper {
        void beepChar(char c);

        void beepWordSeparator();
    }

}
