package com.marcosdiez.spectrumanalyzer;

/**
 * Created by Marcos on 29-Apr-15.
 */
public class Communication {
    public static final int BUFFER_SIZE = 1000;
    static int[] buffer =  new int[BUFFER_SIZE];

    public static String alphabet =" 123456789.;abcdefghijklmnopqrstuvwyxz";
    public static int numWords = 5;

    public static String fixString(String input){
        return input.replace(",",".").toLowerCase();
    }

    public static int[] toNumbers(String input){
        return buffer;
    }

}
