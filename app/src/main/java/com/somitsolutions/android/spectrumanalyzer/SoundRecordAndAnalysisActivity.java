package com.somitsolutions.android.spectrumanalyzer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import ca.uol.aig.fftpack.RealDoubleFFT;


public class SoundRecordAndAnalysisActivity extends Activity{

    int frequency = 8000;
    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    AudioRecord audioRecord;
    private RealDoubleFFT transformer;
    int blockSize;// = 256;
    Button startStopButton;
    boolean started = false;

    RecordAudio recordTask=null;
    ImageView imageViewDisplaySectrum;
    View imageViewScale;
    Bitmap bitmapDisplaySpectrum;

    Canvas canvasDisplaySpectrum;

    Paint paintSpectrumDisplay;
    static SoundRecordAndAnalysisActivity mainActivity;
    int width;
    int height;
    int left_Of_BimapScale;
    int left_Of_DisplaySpectrum;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        blockSize = 256;
        prepareUi();
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        left_Of_BimapScale = imageViewScale.getLeft();
        left_Of_DisplaySpectrum = imageViewDisplaySectrum.getLeft();
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

                    	/*if(width > 512){
                    		bufferReadResult = audioRecord.read(buffer, 0, 512);
                    	}
                    	else{*/
                bufferReadResult = audioRecord.read(buffer, 0, blockSize);
                //}
                if(isCancelled())
                    break;

                for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                    toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
                }

                transformer.ft(toTransform);
                    /*if(width > 512){

                    	publishProgress(toTransform);
                    }*/
                publishProgress(toTransform);
                if(isCancelled())
                    break;
                //return null;
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
            double maxValue =0;
            int maxIndex = 0;

            double[] toTransformZero = toTransform[0];

            float delta = ((float) width) / ((float) ( toTransformZero.length -1 ));
            for (int i = 0; i < toTransformZero.length; i++) {
                float x = delta * i;
                double toAnalyze = toTransformZero[i];
                int downy = (int) (150 - (toAnalyze * 10));
                int upy = 150;
                canvasDisplaySpectrum.drawLine(x, downy, x, upy, paintSpectrumDisplay);

                if(toAnalyze>maxValue){
                    maxValue=toAnalyze;
                    maxIndex = i;
                }
            }
            int fixedValue =(int)maxValue*1000;

            if( fixedValue > 0 ) {
                Log.d("MMM", "Calc:" + width + "/" + height + "/"  + toTransformZero.length + "/" + maxIndex + "/" + fixedValue);
            }
            imageViewDisplaySectrum.invalidate();

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
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }




    public void buttonClicked() {

        if (started == true) {
            started = false;
            startStopButton.setText("StartX");
            recordTask.cancel(true);
            //recordTask = null;
            canvasDisplaySpectrum.drawColor(Color.BLACK);
        } else {
            started = true;
            startStopButton.setText("StopZ");
            recordTask = new RecordAudio();
            recordTask.execute();
        }

    }

    static SoundRecordAndAnalysisActivity getMainActivity(){
        return mainActivity;
    }

    public void onStop(){
        super.onStop();
        	/*started = false;
            startStopButton.setText("Start");*/
        if(recordTask != null){
            recordTask.cancel(true);
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected void prepareUi(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.d("MMM", "Before");
        setContentView(R.layout.main);
        Log.d("MMM", "After");
        imageViewDisplaySectrum = (ImageView) findViewById(R.id.imageViewDisplaySectrum);
        imageViewScale = (View) ((TheScaleImageView) findViewById(R.id.theScaleImageView));
        startStopButton = (Button) findViewById(R.id.startStopButton);
        startStopButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                buttonClicked();
            }
        });

        bitmapDisplaySpectrum = Bitmap.createBitmap(width,(int)300,Bitmap.Config.ARGB_8888);
        imageViewDisplaySectrum.setImageBitmap(bitmapDisplaySpectrum);

        canvasDisplaySpectrum = new Canvas(bitmapDisplaySpectrum);

        paintSpectrumDisplay = new Paint();
        paintSpectrumDisplay.setColor(Color.RED);

        drawBorders();

        paintSpectrumDisplay.setColor(Color.GREEN);
    }

    private void drawBorders() {
        canvasDisplaySpectrum.drawLine(0, 0, 0,
                canvasDisplaySpectrum.getHeight() -1,
                paintSpectrumDisplay);

        canvasDisplaySpectrum.drawLine(0, 0,
                canvasDisplaySpectrum.getWidth() -1,
                0,
                paintSpectrumDisplay);

        canvasDisplaySpectrum.drawLine(
                canvasDisplaySpectrum.getWidth() -1,
                0,
                canvasDisplaySpectrum.getWidth() -1 ,
                canvasDisplaySpectrum.getHeight() -1,
                paintSpectrumDisplay);

        canvasDisplaySpectrum.drawLine(
                0,
                canvasDisplaySpectrum.getHeight() -1 ,
                canvasDisplaySpectrum.getWidth() -1,
                canvasDisplaySpectrum.getHeight() -1,
                paintSpectrumDisplay);


        canvasDisplaySpectrum.drawLine(0, 0,
                canvasDisplaySpectrum.getWidth(),
                canvasDisplaySpectrum.getHeight()
                , paintSpectrumDisplay);
    }

    public void onStart(){
        super.onStart();
        transformer = new RealDoubleFFT(blockSize);
        mainActivity = this;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(recordTask != null){
            recordTask.cancel(true);
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(recordTask != null) {
            recordTask.cancel(true);
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}

