package com.marcosdiez.spectrumanalyzer.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.marcosdiez.spectrumanalyzer.AudioProcessor;
import com.marcosdiez.spectrumanalyzer.CalculateStatistics;
import com.marcosdiez.spectrumanalyzer.R;
import com.marcosdiez.spectrumanalyzer.RecordAudioPlotter;
import com.marcosdiez.spectrumanalyzer.TonePlayer;
import com.marcosdiez.spectrumanalyzer.widgets.TheScaleImageView;
import com.marcosdiez.spectrumanalyzer.widgets.TheSpectrumAnalyzerImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SoundRecordAndAnalysisActivity extends Activity {


    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    public static String TAG = "SoundRecordAndAnalysisActivity";
    Button startStopButton;

    TheSpectrumAnalyzerImageView imageViewDisplaySpectrum;
    TheScaleImageView imageViewScale;
    TextView textViewMeasuredValue;
    private AudioProcessorUi audioProcessorUi = null;

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

        if (audioProcessorUi != null && audioProcessorUi.getStarted()) {
            stopAnalyzer();
        } else {
            startAnalyzer();
        }
    }

    private void startAnalyzer() {
        startStopButton.setText("Stop");
        // imageViewDisplaySpectrum.clearMeasurement();
        audioProcessorUi = new AudioProcessorUi();
        audioProcessorUi.execute();
    }

    private void stopAnalyzer() {
        audioProcessorUi.stop();
        startStopButton.setText("Start");
        if (audioProcessorUi != null) {
            audioProcessorUi.cancel(true);
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

    private class AudioProcessorUi extends AsyncTask<Void, double[], Void> implements RecordAudioPlotter {

        public synchronized void stop() {
            ap.stop();
        }

        public synchronized boolean getStarted() {
            return ap.getStarted();
        }

        AudioProcessor ap = new AudioProcessor(this);

        @Override
        protected Void doInBackground(Void... params) {
            ap.doInBackground();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ap.onStop();
        }

        @Override
        protected void onProgressUpdate(double[]... values) {
            super.onProgressUpdate(values);
            imageViewDisplaySpectrum.invalidate();
            textViewMeasuredValue.setText(imageViewDisplaySpectrum.msg);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            ap.onStop();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            ap.onStop();
        }

        public void backgroundThreadPlot(double[] toTransform, CalculateStatistics statistics) {
            imageViewDisplaySpectrum.plot(toTransform, statistics);
            publishProgress(toTransform);
        }


    }
}

