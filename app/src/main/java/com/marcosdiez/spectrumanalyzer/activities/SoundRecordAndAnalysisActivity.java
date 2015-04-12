package com.marcosdiez.spectrumanalyzer.activities;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.marcosdiez.spectrumanalyzer.CalculateStatistics;
import com.marcosdiez.spectrumanalyzer.R;
import com.marcosdiez.spectrumanalyzer.TonePlayer;
import com.marcosdiez.spectrumanalyzer.widgets.TheScaleImageView;
import com.marcosdiez.spectrumanalyzer.widgets.TheSpectrumAnalyzerImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.uol.aig.fftpack.RealDoubleFFT;


public class SoundRecordAndAnalysisActivity extends Activity {

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    public static String TAG = "SoundRecordAndAnalysisActivity";
    final int blockSize = 256;
    private RealDoubleFFT transformer = new RealDoubleFFT(blockSize);
    int frequency = 8000;
    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    AudioRecord audioRecord;
    Button startStopButton;
    boolean started = false;
    RecordAudio recordTask = null;
    TheSpectrumAnalyzerImageView imageViewDisplaySpectrum;
    TheScaleImageView imageViewScale;
    TextView textViewMeasuredValue;
    private CalculateStatistics statistics = new CalculateStatistics();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        prepareUi();
    }

    protected void prepareUi() {
        setContentView(R.layout.main);
        textViewMeasuredValue = (TextView) findViewById(R.id.textViewMeasuredValue);
        imageViewDisplaySpectrum = (TheSpectrumAnalyzerImageView) findViewById(R.id.imageViewDisplaySectrum);
        imageViewScale = (TheScaleImageView) findViewById(R.id.theScaleImageView);

        startStopButton = getButton(R.id.startStopButton, new OnClickListener() {
            public void onClick(View v) {
                buttonClicked();
            }
        });

        getButton(R.id.button500Hz, new OnClickListener() {
            public void onClick(View v) {
                playSound(500);
            }
        });
        getButton(R.id.button1kHz, new OnClickListener() {
            public void onClick(View v) {
                playSound(1000);
            }
        });
        getButton(R.id.button1500Hz, new OnClickListener() {
            public void onClick(View v) {
                playSound(1500);
            }
        });
        getButton(R.id.button2kHz, new OnClickListener() {
            public void onClick(View v) {
                playSound(2000);
            }
        });
        getButton(R.id.button2500Hz, new OnClickListener() {
            public void onClick(View v) {
                playSound(2500);
            }
        });
        getButton(R.id.button3000Hz, new OnClickListener() {
            public void onClick(View v) {
                playSound(3000);
            }
        });

    }

    private Button getButton(int id, View.OnClickListener c) {
        Button theButton = (Button) findViewById(id);
        theButton.setOnClickListener(c);
        return theButton;
    }

    void playSound(int frequency) {
        int playTimeInMiliSeconds = 1000;
        threadPool.execute(new TonePlayer(playTimeInMiliSeconds, frequency));
    }

    public void buttonClicked() {

        if (started == true) {
            stopAnalyzer();
        } else {
            startAnalyzer();
        }
    }

    private void startAnalyzer() {
        started = true;
        startStopButton.setText("Stop");
        // imageViewDisplaySpectrum.clearMeasurement();
        recordTask = new RecordAudio();
        recordTask.execute();
    }

    private void stopAnalyzer() {
        started = false;
        startStopButton.setText("Start");
        if (recordTask != null) {
            recordTask.cancel(true);
        }
        if (imageViewDisplaySpectrum.canvasDisplaySpectrum != null) {
            imageViewDisplaySpectrum.canvasDisplaySpectrum.drawColor(Color.BLACK);
            imageViewDisplaySpectrum.drawBorders();
        }
    }

    public void onStop() {
        super.onStop();
        stopAnalyzer();
    }

    public void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopAnalyzer();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        stopAnalyzer();
    }

    private class RecordAudio extends AsyncTask<Void, double[], Void> {

        void helper(boolean started) {
            //try {
            int bufferSize = AudioRecord.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding);

            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.DEFAULT, frequency,
                    channelConfiguration, audioEncoding, bufferSize);
            int bufferReadResult;
            short[] buffer = new short[blockSize];
            double[] toTransform = new double[blockSize];
            try {
                audioRecord.startRecording();
            } catch (IllegalStateException e) {
                Log.e("Recording failed", e.toString());

            }
            while (started) {
                bufferReadResult = audioRecord.read(buffer, 0, blockSize);
                if (isCancelled())
                    break;

                for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                    toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
                }

                transformer.ft(toTransform);
                imageViewDisplaySpectrum.plot(toTransform, statistics);
                publishProgress(toTransform);

                if (isCancelled())
                    break;
            }

            try {
                audioRecord.stop();
            } catch (IllegalStateException e) {
                Log.e("Stop failed", e.toString());

            }
        }


        @Override
        protected Void doInBackground(Void... params) {

            if (isCancelled()) {
                return null;
            }
            //try {
            int bufferSize = AudioRecord.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding);

            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.DEFAULT, frequency,
                    channelConfiguration, audioEncoding, bufferSize);
            int bufferReadResult;
            short[] buffer = new short[blockSize];
            double[] toTransform = new double[blockSize];
            try {
                audioRecord.startRecording();
            } catch (IllegalStateException e) {
                Log.e("Recording failed", e.toString());

            }
            while (started) {
                bufferReadResult = audioRecord.read(buffer, 0, blockSize);
                if (isCancelled())
                    break;

                for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                    toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
                }

                transformer.ft(toTransform);
                imageViewDisplaySpectrum.plot(toTransform, statistics);
                publishProgress(toTransform);

                if (isCancelled())
                    break;
            }

            try {
                audioRecord.stop();
            } catch (IllegalStateException e) {
                Log.e("Stop failed", e.toString());

            }

            return null;
        }

        protected void onProgressUpdate(double[]... toTransform) {
            imageViewDisplaySpectrum.invalidate();
            textViewMeasuredValue.setText(imageViewDisplaySpectrum.msg);
        }

        protected void onPostExecute(Void result) {
            try {
                audioRecord.stop();
            } catch (IllegalStateException e) {
                Log.e("Stop failed", e.toString());
            }
            if (recordTask != null) {
                recordTask.cancel(true);
            }
        }

    }

}

