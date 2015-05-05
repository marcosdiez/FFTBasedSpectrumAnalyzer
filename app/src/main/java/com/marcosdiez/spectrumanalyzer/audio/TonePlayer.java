package com.marcosdiez.spectrumanalyzer.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by Marcos on 15-Mar-15.
 */
public class TonePlayer implements Runnable {
    final private static String TAG = "TonePlayer";
    private final byte generatedSnd[] = new byte[64000];
    private int durationInMiliseconds = 3000; // miliseconds
    private int sampleRate = 8000;
    private double freqOfTone = 440; // hz
    private double sample[];
    private int numSamples;
    private int sampleSize;
    private boolean initialized = false;

    public TonePlayer() {

    }

    public TonePlayer(int durationInMiliseconds, int freqOfTone) {
        init(durationInMiliseconds, freqOfTone);
    }

    public void play(int durationInMiliseconds, int freqOfTone) {
        init(durationInMiliseconds, freqOfTone);
        play();
    }

    private void init(int durationInMiliseconds, int freqOfTone) {
        this.durationInMiliseconds = durationInMiliseconds;
        this.freqOfTone = freqOfTone;
        numSamples = (int) (durationInMiliseconds / 1000.0 * sampleRate);
        sampleSize = 2 * numSamples;
        sample = new double[numSamples];

        initialized = false;
    }

    public void run() {
        play();
    }

    public synchronized void play() {
        prepareSound();
        Log.d(TAG, "Playing " + freqOfTone + " Hz for " + durationInMiliseconds + " ms");
        replaySound();
    }

    public void prepareSound() {
        // int sampleRate, int durationInMiliseconds, int freqOfTone, byte[] generatedSnd){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
        }
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        initialized = true;
    }

    public void replaySound() {
        if (!initialized) {
            prepareSound();
        }

        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, sampleSize,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, sampleSize);
        audioTrack.play();

        try {
            Thread.sleep(durationInMiliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
