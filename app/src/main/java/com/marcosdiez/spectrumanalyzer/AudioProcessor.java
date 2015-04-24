package com.marcosdiez.spectrumanalyzer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

import ca.uol.aig.fftpack.RealDoubleFFT;

/**
 * Created by Marcos on 12-Apr-15.
 */
public class AudioProcessor extends AsyncTask<Void, double[], Void> {
    /*
    this class does not do any UI
     */
    public static final int blockSize = 256;
    final private RealDoubleFFT transformer = new RealDoubleFFT(blockSize);
    final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord audioRecord;
    private int frequency = 8000; // Hz
    private boolean started = false;
    private CalculateStatistics statistics = new CalculateStatistics();
    private final static String TAG = "AudioProcessor";

    public CalculateStatistics getStatistics(){ return statistics; }

    public AudioProcessor() {
        super();
    }

    public void setSeekerValue(int id, int value) {
        // guess why we do this ? Java does not have function pointers

        switch (id) {
            case (R.id.seek_filter):
                break;
            case (R.id.seek_iteration):
                Log.d(TAG, "New SeekValue: " + value);
                statistics.setSize(value);
                break;
            default:
               // we don't care. really.
                break;
        }
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onStop();
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        onStop();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        onStop();
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

    @Override
    public Void doInBackground(Void... params) {
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
            doInBackgroundLoop(toTransform);

            publishProgress(toTransform);
        }
        onStop();
        return null;
    }

    public void doInBackgroundLoop(double[] toTransform){

    }
}
