package com.marcosdiez.spectrumanalyzer.audio.Player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.audio.Beeper;
import com.marcosdiez.spectrumanalyzer.text.SmallAsciiAndFrequencies;

/**
 * Created by Marcos on 15-Mar-15.
 */
public class TonePlayer implements Beeper {
    final private static String TAG = "XB-TonePlayer";

    private final int sampleRate = Globals.frequency_limit * 2;
    private final int maxSamples = (int) (Globals.time_of_generated_sound_max / 1000.0 * sampleRate);
    private int numSamples = (int) ((Globals.time_of_generated_sound / 1000.0) * sampleRate);
    private int sampleSize = 2 * numSamples;

    private final byte generatedSnd[][] = new byte[Globals.words + 1][maxSamples * 2];

    private  AudioTrack audioTrack = new AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT, sampleSize,
            AudioTrack.MODE_STATIC);

    public TonePlayer() {
        loadGeneratedSound();
    }

    public void processFrequency(int frequency) {
        int word = SmallAsciiAndFrequencies.toSmallAscii(frequency) - ((int) '0');
        Log.d(TAG, "Freq: " + frequency + " word: " + word);
        playWord(word);
    }

    public void playFrequency(int frequency){
        byte audioData[] = new byte[maxSamples * 2];
        fillAudioData(frequency, audioData);
        playByteArray(audioData);
    }

    public void playWord(int word) {
        byte[] audioData =  generatedSnd[word];
        playByteArray(audioData);
    }

    private synchronized void playByteArray(byte[] audioData) {
        audioTrack.write(audioData, 0, sampleSize);
        audioTrack.reloadStaticData();
        audioTrack.play();

        try {
            Thread.sleep(Globals.time_of_generated_sound);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        audioTrack.stop();
    }

    public void loadGeneratedSound() {
        int delta = Math.abs(Globals.max_frequency - Globals.min_frequency) / Globals.words;
        int frequency = Globals.min_frequency;

        for (int word_num = 0; word_num <= Globals.words; word_num++) {
            byte[] currentAudioData = generatedSnd[word_num];
            Log.d(TAG, "init: word: " + word_num + ", freq: " + frequency + " Hz");
            fillAudioData(frequency, currentAudioData);
            frequency += delta;
        }
    }

    private void fillAudioData(double frequency, byte[] output) {

        // int sampleRate, int durationInMilliseconds, int freqOfTone, byte[] generatedSnd){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            double audioSample = Math.sin(2 * Math.PI * (double) i / ((double) sampleRate / frequency));

            swapSampleEndianess(output, i, audioSample);
        }
    }

    private void swapSampleEndianess(byte[] output, int i, double audioSample) {
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.

        final short val = (short) ((audioSample * 32767));
        // in 16 bit wav PCM, first byte is the low order byte
        output[2*i] = (byte) (val & 0x00ff);
        output[2*i+1] = (byte) ((val & 0xff00) >>> 8);
    }

//    private void fillAudioData2(double frequency, byte[] output) {
//        double[] sample = new double[maxSamples];
//        // int sampleRate, int durationInMilliseconds, int freqOfTone, byte[] generatedSnd){
//        // fill out the array
//        for (int i = 0; i < numSamples; ++i) {
//            sample[i] = Math.sin(2 * Math.PI * (double) i / ((double) sampleRate / frequency));
//        }
//        // convert to 16 bit pcm sound array
//        // assumes the sample buffer is normalised.
//        int idx = 0;
//        for (int sample_idx = 0; sample_idx < numSamples; sample_idx++) {
//            final double dVal = sample[sample_idx];
//            //for (final double dVal : sample) {
//            // scale to maximum amplitude
//            final short val = (short) ((dVal * 32767));
//            // in 16 bit wav PCM, first byte is the low order byte
//            output[idx++] = (byte) (val & 0x00ff);
//            output[idx++] = (byte) ((val & 0xff00) >>> 8);
//        }
//    }
    //    private int durationInMilliseconds = 3000; // miliseconds
//    private int numSamples;
//    private int sampleSize;
//    private boolean initialized = false;


//    public TonePlayer(int durationInMilliseconds, int freqOfTone) {
//        init(durationInMilliseconds, freqOfTone);
//    }
//
//    public void processFrequency(int frequency) {
//        play(Globals.time_of_generated_sound, frequency);
//    }
//
//    public void play(int durationInMilliseconds, int freqOfTone) {
//        init(durationInMilliseconds, freqOfTone);
//        play();
//    }
//
//    private void init(int durationInMilliseconds, int freqOfTone) {
//        this.durationInMilliseconds = durationInMilliseconds;
//        this.freqOfTone = freqOfTone;
//        numSamples = (int) ((this.durationInMilliseconds / 1000.0) * sampleRate);
//        sampleSize = 2 * numSamples;
//        initialized = false;
//    }
//
//    public void run() {
//        play();
//    }
//
//    public synchronized void play() {
//        prepareSound();
//        Log.d(TAG, "Playing " + freqOfTone + " Hz for " + durationInMilliseconds + " ms");
//        replaySound();
//    }
//
//    public void prepareSound() {
//        // int sampleRate, int durationInMilliseconds, int freqOfTone, byte[] generatedSnd){
//        // fill out the array
//        for (int i = 0; i < numSamples; ++i) {
//            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
//        }
//        // convert to 16 bit pcm sound array
//        // assumes the sample buffer is normalised.
//        int idx = 0;
//        for (int sample_idx = 0; sample_idx < numSamples; sample_idx++) {
//            final double dVal = sample[sample_idx];
//            //for (final double dVal : sample) {
//            // scale to maximum amplitude
//            final short val = (short) ((dVal * 32767));
//            // in 16 bit wav PCM, first byte is the low order byte
//            generatedSnd[idx++] = (byte) (val & 0x00ff);
//            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
//        }
//        initialized = true;
//    }


}
