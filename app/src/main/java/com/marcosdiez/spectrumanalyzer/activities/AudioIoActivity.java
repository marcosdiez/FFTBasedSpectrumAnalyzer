package com.marcosdiez.spectrumanalyzer.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.marcosdiez.spectrumanalyzer.Globals;
import com.marcosdiez.spectrumanalyzer.R;
import com.marcosdiez.spectrumanalyzer.audio.AudioIoPlayer;
import com.marcosdiez.spectrumanalyzer.audio.AudioProcessor;
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
        analise();
    }

    private String getVersion() {
        try {
            return getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }


    protected void prepareUi() {
        setContentView(R.layout.audio_io);

        prepareSeeker(R.id.seek_min_frequency, "Min Freq. (Hz): ", Globals.frequency_limit, Globals.min_frequency);
        prepareSeeker(R.id.seek_max_frequency, "Max Freq. (Hz): ", Globals.frequency_limit, Globals.max_frequency);
        prepareSeeker(R.id.seek_time, "Max Time (Ms): ", Globals.time_of_generated_sound_max, Globals.time_of_generated_sound);
        prepareSeeker(R.id.seek_words, "Words: ", Globals.words_max, Globals.words);
        prepareSeeker(R.id.seek_filter, "Volume Filter: ", Globals.minumum_audio_volume_to_be_considered_max, Globals.minumum_audio_volume_to_be_considered);
//        prepareSeeker(R.id.seek_iteration, "Num Samples: ", Globals.num_samples_max, Globals.num_samples);

        outputGeneratingTextView = (TextView) findViewById(R.id.outputGeneratingTextView);
        outputCapturingTextView = (TextView) findViewById(R.id.outputCapturingTextView);


        ((TextView) findViewById(R.id.txt_version)).setText(" v" + getVersion());


        getButton(R.id.button_send_text, new View.OnClickListener() {
            public void onClick(View v) {
                threadPool.execute(player);
                Misc.toast("Playing !");
            }
        });


        getButton(R.id.button_clear, new View.OnClickListener() {
            public void onClick(View v) {
                outputCapturingTextView.setText("");
            }
        });


//        getButton(R.id.button_spectrum_analyzer, new View.OnClickListener() {
//            public void onClick(View v) {
//                loadSpectrumAnalyzer();
//            }
//        });


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
                if (final_id != R.id.seek_filter && progress == 0) {
                    seekBar.setProgress(1);
                } else {
                    updateGlobalVariable(progress);
                    theValue.setText(progress + "");
                }
            }

            private void updateGlobalVariable(int progress) {
                switch (final_id) {
                    case R.id.seek_min_frequency:
                        Globals.min_frequency = progress;
                        break;
                    case R.id.seek_max_frequency:
                        Globals.max_frequency = progress;
                        break;
                    case R.id.seek_time:
                        Globals.time_of_generated_sound = progress;
                        break;
                    case R.id.seek_words:
                        Globals.words = progress;
                        break;
                    case R.id.seek_filter:
                        Globals.minumum_audio_volume_to_be_considered = progress;
                        break;
//                    case R.id.seek_iteration:
//                        Globals.num_samples = progress;
//                        break;
                    default:
                        break;
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
        String lastMsg = "";

        @Override
        protected void onProgressUpdate(double[]... toTransform) {
            super.onProgressUpdate(toTransform);
            String msg = super.getStatisticsMsg();
            if (msg != lastMsg) {
                lastMsg = msg;
                outputCapturingTextView.setText(msg);
            }
        }

        @Override
        public void doInBackgroundLoop(double[] toTransform) {
        }

    }

}
