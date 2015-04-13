package com.marcosdiez.spectrumanalyzer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import ca.uol.aig.fftpack.RealDoubleFFT;

/**
 * Created by Marcos on 12-Apr-15.
 */
public class AudioProcessor {
    /*
    this class does not do any UI
     */
    final int blockSize = 256;
    final private RealDoubleFFT transformer = new RealDoubleFFT(blockSize);
    final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private RecordAudioPlotter recordAudioPlotter;
    private AudioRecord audioRecord;
    private int frequency;
    private boolean started = false;
    private CalculateStatistics statistics = new CalculateStatistics();

    public AudioProcessor(int frequency, RecordAudioPlotter recordAudioPlotter) {
        super();
        this.recordAudioPlotter = recordAudioPlotter;
        this.frequency = frequency;
    }

    public void onStop() {
        stop();
        try {
            audioRecord.stop();
        } catch (IllegalStateException e) {
            Log.e("Stop failed", e.toString());
        }
    }

    public synchronized void stop() {
        started = false;
    }

    public synchronized boolean getStarted() {
        return started;
    }

    public void doInBackground() {
        int bufferSize = AudioRecord.getMinBufferSize(frequency,
                channelConfiguration, audioEncoding);

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.DEFAULT, frequency, channelConfiguration, audioEncoding, bufferSize);
        int bufferReadResult;
        short[] buffer = new short[blockSize];
        double[] toTransform = new double[blockSize];
        try {
            audioRecord.startRecording();
        } catch (IllegalStateException e) {
            Log.e("Recording failed", e.toString());

        }
        started = true;
        while (started) {
            bufferReadResult = audioRecord.read(buffer, 0, blockSize);

            for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
            }

            transformer.ft(toTransform);
            statistics.calculateStatistics(toTransform);
            if (recordAudioPlotter != null) {
                recordAudioPlotter.backgroundThreadPlot(toTransform, statistics);
            }
        }
        onStop();
    }
}