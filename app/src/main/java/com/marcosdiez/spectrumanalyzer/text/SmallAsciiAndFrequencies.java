package com.marcosdiez.spectrumanalyzer.text;

import com.marcosdiez.spectrumanalyzer.Globals;

/**
 * Created by Marcos on 24-May-15.
 */
public class SmallAsciiAndFrequencies {
    /*
    0 -> 1000 Hz
    1 -> 1400 Hz
    2 -> 1800 Hz
    3 -> 2200 Hz
    4 -> 2600 Hz
    5 -> 3000 Hz
     */
    public static int toFrequency(char c) {
        int delta = Math.abs(Globals.max_frequency - Globals.min_frequency);
        int frequencyIncrement = delta / Globals.words;
        int beep_value = toInt(c);
        int frequency = Globals.min_frequency + frequencyIncrement * beep_value;
        return frequency;
    }

    public static char toSmallAsciiNoException(int frequency) {
        try {
            return toSmallAscii(frequency);
        } catch (IllegalArgumentException e) {
            return '*';
        }

    }

    public static char toSmallAscii(int frequency) {
        int delta = Math.abs(Globals.max_frequency - Globals.min_frequency);
        int frequencyIncrement = delta / Globals.words;

        int beep_value = (frequency - Globals.min_frequency) / frequencyIncrement;
        return toChar(beep_value);
    }

    private static int toInt(char theChar) {
        switch (theChar) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            default:
                throw new IllegalArgumentException("invalid char [" + theChar + "]");
        }
    }

    private static char toChar(int number) {
        switch (number) {
            case 0:
                return '0';
            case 1:
                return '1';
            case 2:
                return '2';
            case 3:
                return '3';
            case 4:
                return '4';
            case 5:
                return '5';
            case 6:
                return '6';
            case 7:
                return '7';
            case 8:
                return '8';
            case 9:
                return '9';
            default:
                throw new IllegalArgumentException("invalid number [" + number + "]");
        }
    }
}
