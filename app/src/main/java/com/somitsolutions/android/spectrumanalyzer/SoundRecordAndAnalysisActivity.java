package com.somitsolutions.android.spectrumanalyzer;

import android.app.Activity;
import android.content.Context;
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
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ca.uol.aig.fftpack.RealDoubleFFT;


public class SoundRecordAndAnalysisActivity extends Activity implements OnClickListener{

    int frequency = 8000;
    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    AudioRecord audioRecord;
    private RealDoubleFFT transformer;
    int blockSize;// = 256;
    Button startStopButton;
    boolean started = false;

    RecordAudio recordTask;
    ImageView imageViewDisplaySectrum;
    MyImageView imageViewScale;
    Bitmap bitmapDisplaySpectrum;

    Canvas canvasDisplaySpectrum;


    Paint paintSpectrumDisplay;
    Paint paintScaleDisplay;
    static SoundRecordAndAnalysisActivity mainActivity;
    LinearLayout main;
    int width;
    int height;
    int left_Of_BimapScale;
    int left_Of_DisplaySpectrum;
    private final static int ID_BITMAPDISPLAYSPECTRUM = 1;
    private final static int ID_IMAGEVIEWSCALE = 2;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        //Point size = new Point();
        //display.get(size);
        width = display.getWidth();
        height = display.getHeight();
    	/*if (width > 512){
    		blockSize = 512;
    	}
    	else{*/
        blockSize = 256;
        //}
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        //left_Of_BimapScale = main.getC.getLeft();
        MyImageView  scale = (MyImageView)main.findViewById(ID_IMAGEVIEWSCALE);
        ImageView bitmap = (ImageView)main.findViewById(ID_BITMAPDISPLAYSPECTRUM);
        left_Of_BimapScale = scale.getLeft();
        left_Of_DisplaySpectrum = bitmap.getLeft();
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

            float delta = 2 * width / 512;

            //if (width > 512){
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
                Log.d("RecordingProgress", "Calc:" + width + "/" + maxIndex + "/" + fixedValue);
            }
            imageViewDisplaySectrum.invalidate();
//            }

        }

        protected void onPostExecute(Void result) {
            try{
                audioRecord.stop();
            }
            catch(IllegalStateException e){
                Log.e("Stop failed", e.toString());

            }
            recordTask.cancel(true);
            //}
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }




    public void onClick(View v) {

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
        //if(recordTask != null){
        recordTask.cancel(true);
        //}
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onStart(){

        super.onStart();
        main = new LinearLayout(this);
        main.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT));
        main.setOrientation(LinearLayout.VERTICAL);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        transformer = new RealDoubleFFT(blockSize);

        imageViewDisplaySectrum = new ImageView(this);
        // if(width > 512){
            bitmapDisplaySpectrum = Bitmap.createBitmap(width,(int)300,Bitmap.Config.ARGB_8888);
        // }
//        else{
//            bitmapDisplaySpectrum = Bitmap.createBitmap((int)256,(int)150,Bitmap.Config.ARGB_8888);
//        }
        LinearLayout.LayoutParams layoutParams_imageViewScale = null;
        //Bitmap scaled = Bitmap.createScaledBitmap(bitmapDisplaySpectrum, 320, 480, true);
        canvasDisplaySpectrum = new Canvas(bitmapDisplaySpectrum);
        //canvasDisplaySpectrum = new Canvas(scaled);
        paintSpectrumDisplay = new Paint();
        paintSpectrumDisplay.setColor(Color.GREEN);
        imageViewDisplaySectrum.setImageBitmap(bitmapDisplaySpectrum);
        if(width >512){
            //imageViewDisplaySectrum.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams layoutParams_imageViewDisplaySpectrum=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            ((MarginLayoutParams) layoutParams_imageViewDisplaySpectrum).setMargins(0, 600, 0, 0);
            imageViewDisplaySectrum.setLayoutParams(layoutParams_imageViewDisplaySpectrum);
            layoutParams_imageViewScale= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //layoutParams_imageViewScale.gravity = Gravity.CENTER_HORIZONTAL;
            //((MarginLayoutParams) layoutParams_imageViewScale).setMargins(0, 20, 0, 0);

        }


        imageViewDisplaySectrum.setId(ID_BITMAPDISPLAYSPECTRUM);
        main.addView(imageViewDisplaySectrum);


        //((MarginLayoutParams) layoutParams_imageViewScale).setMargins(0, 20, 0, 20);

        imageViewScale = new MyImageView(this);
        imageViewScale.setLayoutParams(layoutParams_imageViewScale);
        imageViewScale.setId(ID_IMAGEVIEWSCALE);

        //imageViewScale.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        main.addView(imageViewScale);

        startStopButton = new Button(this);
        startStopButton.setText("StartW");
        startStopButton.setOnClickListener(this);
        startStopButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        main.addView(startStopButton);

        setContentView(main);
        //recordTask = new RecordAudio();


            /*left_Of_BimapScale = main.getChildAt(1).getLeft();*/

        mainActivity = this;

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //if(recordTask != null){
        recordTask.cancel(true);
        //}
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        recordTask.cancel(true);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    //Custom Imageview Class
    public class MyImageView extends ImageView {
        Paint paintScaleDisplay;
        Bitmap bitmapScale;
        Canvas canvasScale;
        //Bitmap scaled;
        public MyImageView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            // if(width >512){
            bitmapScale = Bitmap.createBitmap(width,(int)50,Bitmap.Config.ARGB_8888);
            // }

            paintScaleDisplay = new Paint();
            paintScaleDisplay.setColor(Color.WHITE);
            paintScaleDisplay.setStyle(Paint.Style.FILL);

            canvasScale = new Canvas(bitmapScale);

            setImageBitmap(bitmapScale);
            invalidate();


        }
        @Override
        protected void onDraw(Canvas canvas)
        {
            // TODO Auto-generated method stub
            super.onDraw(canvas);

            // int x_Of_BimapScale = bitmapScale.

            //if(width > 512){
            float delta = width / 4;
            float delta_by_8 = delta/8;

            canvasScale.drawLine(0, 30,  width, 30, paintScaleDisplay);
            for(float i = 0, j = 0; i< width; i=i+delta, j++){
                for (float k = i; k<(i+delta); k=k+delta_by_8){
                    canvasScale.drawLine(k, 30, k, 25, paintScaleDisplay);
                }
                canvasScale.drawLine(i, 40, i, 25, paintScaleDisplay);
                String text = Integer.toString((int)j) + " KHz";
                canvasScale.drawText(text, i, 45, paintScaleDisplay);
            }
            canvas.drawBitmap(bitmapScale, 0, 0, paintScaleDisplay);
            //}


            //canvas.drawBitmap(bitmapScale, 0, 400, paintScaleDisplay);
            //invalidate();
        }

    }

}

