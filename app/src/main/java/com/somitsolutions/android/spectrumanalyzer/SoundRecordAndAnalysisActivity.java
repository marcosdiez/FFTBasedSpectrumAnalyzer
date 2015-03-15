package com.somitsolutions.android.spectrumanalyzer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import ca.uol.aig.fftpack.RealDoubleFFT;


public class SoundRecordAndAnalysisActivity extends Activity{

    public static String TAG = "SoundRecordAndAnalysisActivity";

    int frequency = 8000;
    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    AudioRecord audioRecord;
    private RealDoubleFFT transformer;
    int blockSize;// = 256;

    Button startStopButton;
    boolean started = false;

    RecordAudio recordTask=null;
    TheSpectrumAnalizerImageView imageViewDisplaySectrum;
    TheScaleImageView imageViewScale;
    TextView textViewMeasuredValue;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blockSize = 256;
        prepareUi();
    }

    protected void prepareUi(){
        setContentView(R.layout.main);
        textViewMeasuredValue = (TextView) findViewById(R.id.textViewMeasuredValue);
        imageViewDisplaySectrum = (TheSpectrumAnalizerImageView) findViewById(R.id.imageViewDisplaySectrum);
        imageViewScale = (TheScaleImageView) findViewById(R.id.theScaleImageView);
        startStopButton = (Button) findViewById(R.id.startStopButton);
        startStopButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                buttonClicked();
            }
        });
    }

    private class RecordAudio extends AsyncTask<Void, double[], Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if(isCancelled()){
                return null;
            }
            //try {
            int bufferSize = AudioRecord.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding);
                    /*AudioRecord */audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.DEFAULT, frequency,
                    channelConfiguration, audioEncoding, bufferSize);
            int bufferReadResult;
            short[] buffer = new short[blockSize];
            double[] toTransform = new double[blockSize];
            try{
                audioRecord.startRecording();
            }
            catch(IllegalStateException e){
                Log.e("Recording failed", e.toString());

            }
            while (started) {
                bufferReadResult = audioRecord.read(buffer, 0, blockSize);
                if(isCancelled())
                    break;

                for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                    toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
                }

                transformer.ft(toTransform);
                publishProgress(toTransform);
                if(isCancelled())
                    break;
            }

            try{
                audioRecord.stop();
            }
            catch(IllegalStateException e){
                Log.e("Stop failed", e.toString());

            }

            return null;
        }

        protected void onProgressUpdate(double[]... toTransform) {
            double[] toTransformZero = toTransform[0];
            imageViewDisplaySectrum.plot(toTransformZero);
        }

        protected void onPostExecute(Void result) {
            try{
                audioRecord.stop();
            }
            catch(IllegalStateException e){
                Log.e("Stop failed", e.toString());

            }
            if( recordTask != null) {
                recordTask.cancel(true);
            }
        }

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
        startStopButton.setText("StopZ");
        recordTask = new RecordAudio();
        recordTask.execute();
    }

    private void stopAnalyzer() {
        started = false;
        startStopButton.setText("StartX");
        if(recordTask!=null) {
            recordTask.cancel(true);
        }
        if(imageViewDisplaySectrum.canvasDisplaySpectrum!=null) {
            imageViewDisplaySectrum.canvasDisplaySpectrum.drawColor(Color.BLACK);
            imageViewDisplaySectrum.drawBorders();
        }
    }

    public void onStop(){
        super.onStop();
        stopAnalyzer();
    }

    public void onStart(){
        super.onStart();
        transformer = new RealDoubleFFT(blockSize);
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

}

