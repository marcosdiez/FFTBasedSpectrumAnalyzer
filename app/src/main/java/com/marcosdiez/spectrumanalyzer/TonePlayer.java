package com.marcosdiez.spectrumanalyzer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by Marcos on 15-Mar-15.
 */
public class TonePlayer implements Runnable {
    private  int duration = 3; // seconds
    private  int sampleRate = 8000;
    private  double freqOfTone = 440; // hz
    private final byte generatedSnd[] = new byte[64000];
    private boolean initialized = false;

    public TonePlayer(int duration, int freqOfTone){
        this.duration = duration;
        this.freqOfTone = freqOfTone;
        initialized=false;
    }

    public void play(int duration, int freqOfTone){
        this.duration = duration;
        this.freqOfTone = freqOfTone;
        initialized=false;
        play();
    }

    public void run(){
        play();
    }

    public void play(){
        prepareSound();
        replaySound();
    }

    public void prepareSound(){ // int sampleRate, int duration, int freqOfTone, byte[] generatedSnd){
        int numSamples = duration * sampleRate;
        double sample[] = new double[numSamples];
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
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
        initialized=true;
    }

    public void replaySound(){
        if(!initialized){
            prepareSound();
        }
        //int sampleRate, int duration, byte[] generatedSnd){
        int numSamples = duration * sampleRate;
        int sampleSize = 2 * numSamples;

        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, sampleSize,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, sampleSize);
        audioTrack.play();
    }
}
