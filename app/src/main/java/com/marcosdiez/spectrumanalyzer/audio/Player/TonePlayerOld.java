package com.marcosdiez.spectrumanalyzer.audio.Player;

/**
 * Created by Marcos on 24-May-15.
 */
public class TonePlayerOld implements Runnable {

    private int last_frequency = 0;
    TonePlayer tonePlayer;

    public TonePlayerOld(int frequency) {
        last_frequency = frequency;
        tonePlayer = new TonePlayer();
    }

    public void run() {
        tonePlayer.processFrequency(last_frequency);
    }
}
