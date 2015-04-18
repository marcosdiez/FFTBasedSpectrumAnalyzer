package com.marcosdiez.spectrumanalyzer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.marcosdiez.spectrumanalyzer.AudioIoPlayer;
import com.marcosdiez.spectrumanalyzer.AudioProcessor;
import com.marcosdiez.spectrumanalyzer.R;
import com.marcosdiez.spectrumanalyzer.RecordAudioPlotter;
import com.marcosdiez.spectrumanalyzer.Toaster;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Marcos on 29-Mar-15.
 */
public class AudioIoActivity extends Activity {

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    AudioIoPlayer player = new AudioIoPlayer();
    AudioProcessorUi audioProcessorUi = new AudioProcessorUi();
    TextView outputCapturingTextView;
    TextView outputGeneratingTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toaster.init(getApplicationContext());
        prepareUi();
    }

    protected void prepareUi() {
        setContentView(R.layout.audio_io);

        prepareSeeker(R.id.seek_min_frequency, "Min Freq. (Hz): ", 4000, 500);
        prepareSeeker(R.id.seek_max_frequency, "Max Freq. (Hz): ", 4000, 3000);
        prepareSeeker(R.id.seek_time, "Max Time (Ms): ", 5000, 1000);
        prepareSeeker(R.id.seek_words, "Words: ", 10, 5);
        prepareSeeker(R.id.seek_filter, "Filter: ", 100, 3);
        prepareSeeker(R.id.seek_iteration, "Iterations: ", 100, 20);

        outputGeneratingTextView = (TextView) findViewById(R.id.outputGeneratingTextView);
        outputCapturingTextView = (TextView) findViewById(R.id.outputCapturingTextView);

        getButton(R.id.button_clear, new View.OnClickListener() {
            public void onClick(View v) {
                outputCapturingTextView.setText("");
            }
        });

        getButton(R.id.button_start_capture, new
                View.OnClickListener() {
                    public void onClick(View v) {
                        analise();
                    }
                });

        getButton(R.id.button_spectrum_analyzer, new View.OnClickListener() {
            public void onClick(View v) {
                loadSpectrumAnalyzer();
            }
        });

        getButton(R.id.button_send_text, new View.OnClickListener() {
            public void onClick(View v) {
                threadPool.execute(player);
                Toaster.toast("Playing !");
            }
        });

    }

    private void analise() {
        audioProcessorUi.execute();
    }

    private Button getButton(int id, View.OnClickListener c) {
        Button theButton = (Button) findViewById(id);
        theButton.setOnClickListener(c);
        return theButton;
    }


    private void loadSpectrumAnalyzer() {
        Intent intent = new Intent(this, SoundRecordAndAnalysisActivity.class);
        startActivity(intent);
    }

    private void prepareSeeker(int id, String title, int maxValue, int initialValue) {
        final int final_id = id;
        View mySeeker = findViewById(id);
        TextView theName = (TextView) mySeeker.findViewById(R.id.my_seeker_textview_name);
        final TextView theValue = (TextView) mySeeker.findViewById(R.id.my_seeker_textview_value);
        SeekBar theSeekBar = (SeekBar) mySeeker.findViewById(R.id.my_seeker_seekbar);

       

        theName.setText(title);
        theSeekBar.setMax(maxValue);
        theSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    seekBar.setProgress(1);
                } else {
                    theValue.setText(progress + "");
                    player.setSeekerValue(final_id, progress); // guess which language does not have function pointers ?
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        theSeekBar.setProgress(initialValue);
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
            outputCapturingTextView.setText(ap.getStatistics().createMsg());
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

        public void backgroundThreadPlot(double[] toTransform) {
            publishProgress(toTransform);
        }


    }

}
