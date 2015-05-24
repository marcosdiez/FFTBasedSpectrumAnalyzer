package com.marcosdiez.spectrumanalyzer.audio;

import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.text.AsciiAndSmallAscii;
import com.marcosdiez.spectrumanalyzer.text.SmallAsciiAndFrequencies;

/**
 * Created by Marcos on 29-Apr-15.
 */
public class Communication {

    public static String fixString(String input) {
        return input.replace(",", ".").toLowerCase();
    }

    public static void player(String sentence, Beeper beeper) {
        String newSentence = fixString(sentence);
        int size = newSentence.length();
        for (int i = 0; i < size; i++) {
            char c = newSentence.charAt(i);
            String smallAsciiWord = AsciiAndSmallAscii.toSmallAscii(c);
            playerHelper(smallAsciiWord, beeper);
        }
    }

    private static void playerHelper(String smallAsciiWord, Beeper b) {
        int size = smallAsciiWord.length();
        b.processFrequency(Globals.max_frequency);
        for (int i = 0; i < size; i++) {
            char letterOfASmallAsciiWord = smallAsciiWord.charAt(i);
            int frequency = SmallAsciiAndFrequencies.toFrequency(letterOfASmallAsciiWord);
            b.processFrequency(frequency);
        }
    }

    public interface Beeper {
        void processFrequency(int frequency);
    }

}
