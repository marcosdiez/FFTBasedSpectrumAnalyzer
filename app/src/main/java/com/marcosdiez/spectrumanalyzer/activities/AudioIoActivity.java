package com.marcosdiez.spectrumanalyzer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.R;
import com.marcosdiez.spectrumanalyzer.audio.AudioIoPlayer;
import com.marcosdiez.spectrumanalyzer.audio.AudioProcessor;
import com.marcosdiez.spectrumanalyzer.audio.CalculateStatistics;
import com.marcosdiez.spectrumanalyzer.util.Misc;

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
        Globals.setContext(getApplicationContext());
        prepareUi();
    }

    protected void prepareUi() {
        setContentView(R.layout.audio_io);

        prepareSeeker(R.id.seek_min_frequency, "Min Freq. (Hz): ", 4000, 500);
        prepareSeeker(R.id.seek_max_frequency, "Max Freq. (Hz): ", 4000, 3000);
        prepareSeeker(R.id.seek_time, "Max Time (Ms): ", 5000, 1000);
        prepareSeeker(R.id.seek_words, "Words: ", 10, 2);
        prepareSeeker(R.id.seek_filter, "Audio Filter: ", CalculateStatistics.maxMinumumAudioVolumeWeConsider, CalculateStatistics.initialMinumumAudioVolumeWeConsider);
        prepareSeeker(R.id.seek_iteration, "Iterations: ", CalculateStatistics.maxSamples, CalculateStatistics.initialNumSamples);

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
                Misc.toast("Playing !");
            }
        });

    }

    private void analise() {
        audioProcessorUi.execute();
        Misc.toast("Audio Processor running in Background");
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
                    audioProcessorUi.setSeekerValue(final_id, progress);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        theSeekBar.setProgress(initialValue);
    }

    private class AudioProcessorUi extends AudioProcessor {
        @Override
        protected void onProgressUpdate(double[]... toTransform) {
            super.onProgressUpdate(toTransform);
            outputCapturingTextView.setText(super.getStatisticsMsg());
        }

        @Override
        public  void doInBackgroundLoop(double[] toTransform){}

    }

}
