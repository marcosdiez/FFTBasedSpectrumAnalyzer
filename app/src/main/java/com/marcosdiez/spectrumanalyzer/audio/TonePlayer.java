package com.marcosdiez.spectrumanalyzer.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.marcosdiez.spectrumanalyzer.Globals;

/**
 * Created by Marcos on 15-Mar-15.
 */
public class TonePlayer implements Runnable , Communication.Beeper {
    final private static String TAG = "XB-TonePlayer";

    private final int sampleRate = Globals.frequency_limit * 2;
    private final int maxSamples = (int) (Globals.time_of_generated_sound_max / 1000.0 * sampleRate);
    private final double[] sample = new double[maxSamples];
    private final byte generatedSnd[] = new byte[maxSamples * 2];





    private int durationInMilliseconds = 3000; // miliseconds
    private double freqOfTone = 440; // hz
    private int numSamples;
    private int sampleSize;
    private boolean initialized = false;

    public TonePlayer() {
    }

    public TonePlayer(int freqOfTone, int durationInMilliseconds) {
        init(freqOfTone, durationInMilliseconds);
    }

    public void processFrequency(int freqOfTone){
        play(freqOfTone, Globals.time_of_generated_sound);
    }

    public void play(int freqOfTone, int durationInMilliseconds) {
        init(freqOfTone, durationInMilliseconds);
        play();
    }

    private void init(int freqOfTone, int durationInMilliseconds) {
        this.durationInMilliseconds = durationInMilliseconds;
        this.freqOfTone = freqOfTone;
        numSamples = (int) ((this.durationInMilliseconds / 1000.0) * sampleRate);
        sampleSize = 2 * numSamples;
        initialized = false;
    }

    public void run() {
        play();
    }

    public synchronized void play() {
        prepareSound();
        // Log.d(TAG, "Playing " + freqOfTone + " Hz for " + durationInMilliseconds + " ms");
        replaySound();
    }

    public void prepareSound() {
        // int sampleRate, int durationInMilliseconds, int freqOfTone, byte[] generatedSnd){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
        }
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (int sample_idx = 0; sample_idx < numSamples; sample_idx++) {
            final double dVal = sample[sample_idx];
            //for (final double dVal : sample) {
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
            Thread.sleep(durationInMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
