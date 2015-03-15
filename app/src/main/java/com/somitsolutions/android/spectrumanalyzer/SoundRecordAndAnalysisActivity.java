package com.somitsolutions.android.spectrumanalyzer;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
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
    ImageView imageViewDisplaySectrum;
    View imageViewScale;
    Bitmap bitmapDisplaySpectrum;

    Canvas canvasDisplaySpectrum;

    Paint paintSpectrumDisplay;
    int width;
    int height;


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

            int myWidth = canvasDisplaySpectrum.getWidth();
            int myHeight = canvasDisplaySpectrum.getHeight();

            double[] toTransformZero = toTransform[0];

            paintSpectrumDisplay.setColor(Color.GREEN);

            float delta = ((float) myWidth) / ((float) ( toTransformZero.length -1 ));
            for (int i = 0; i < toTransformZero.length; i++) {
                float x = delta * i;
                double toAnalyze = toTransformZero[i];
                int downy = (int) (myHeight/2 - (toAnalyze * 10));
                int upy = myHeight/2;
                canvasDisplaySpectrum.drawLine(x, downy, x, upy, paintSpectrumDisplay);

                if(toAnalyze>maxValue){
                    maxValue=toAnalyze;
                    maxIndex = i;
                }
            }

            int fixedValue =(int)maxValue*1000;

            if( fixedValue > 0 ) {
                Log.d(TAG, "Calc:" +  myWidth + "/"
                        + myHeight + "/" +
                        toTransformZero.length + "/" + maxIndex + "/" + fixedValue);
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
        }

    }




    public void buttonClicked() {

        if (started == true) {
            started = false;
            startStopButton.setText("StartX");
            recordTask.cancel(true);
            //recordTask = null;
            canvasDisplaySpectrum.drawColor(Color.BLACK);
            drawBorders();
        } else {
            started = true;
            startStopButton.setText("StopZ");
            recordTask = new RecordAudio();
            recordTask.execute();
        }

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
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);
        imageViewDisplaySectrum = (ImageView) findViewById(R.id.imageViewDisplaySectrum);
        imageViewScale = (View) ((TheScaleImageView) findViewById(R.id.theScaleImageView));
        startStopButton = (Button) findViewById(R.id.startStopButton);
        startStopButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                buttonClicked();
            }
        });

        initDisplaySpectrum();
    }

    private void initDisplaySpectrum() {
        bitmapDisplaySpectrum = Bitmap.createBitmap(width, 300, Bitmap.Config.ARGB_8888);
        imageViewDisplaySectrum.setImageBitmap(bitmapDisplaySpectrum);
        canvasDisplaySpectrum = new Canvas(bitmapDisplaySpectrum);
        paintSpectrumDisplay = new Paint();
        drawBorders();
    }

    private void drawBorders() {
        paintSpectrumDisplay.setColor(Color.WHITE);

        int maxWidth = canvasDisplaySpectrum.getWidth() -1;
        int maxHeight = canvasDisplaySpectrum.getHeight() -1;

        canvasDisplaySpectrum.drawLine(0, maxHeight/2,
                maxWidth,
                maxHeight/2,
                paintSpectrumDisplay);

        paintSpectrumDisplay.setColor(Color.RED);
        canvasDisplaySpectrum.drawLine(0, 0, 0,
                maxHeight,
                paintSpectrumDisplay);

        canvasDisplaySpectrum.drawLine(0, 0,
                maxWidth,
                0,
                paintSpectrumDisplay);

        canvasDisplaySpectrum.drawLine(
                maxWidth,
                0,
                maxWidth,
                maxHeight,
                paintSpectrumDisplay);

        canvasDisplaySpectrum.drawLine(
                0,
                maxHeight,
                maxWidth,
                maxHeight,
                paintSpectrumDisplay);


        canvasDisplaySpectrum.drawLine(0, 0,
                maxWidth,
                maxHeight,
                paintSpectrumDisplay);
    }

    public void onStart(){
        super.onStart();
        transformer = new RealDoubleFFT(blockSize);
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

