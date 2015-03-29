package com.marcosdiez.spectrumanalyzer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Marcos on 09-Mar-15.
 */
public class TheSpectrumAnalyzerImageView extends ImageView {
    public Bitmap bitmapDisplaySpectrum=null;
    public Canvas canvasDisplaySpectrum=null;
    public Paint paintSpectrumDisplay=null;

    TextView output=null;

    public final int maxAge = 100;
    int height = 0;
    int width = 0;
    boolean initialized = false;

    public static String TAG = "TheSpectrumAnalizerImageView";

    public void setOutput(TextView output){
        this.output=output;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(initialized){
            return;
        }

        width = this.getWidth();
        height = this.getHeight();
        if(width == 0) {
            return;
        }
        bitmapDisplaySpectrum = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        setImageBitmap(bitmapDisplaySpectrum);
        canvasDisplaySpectrum = new Canvas(bitmapDisplaySpectrum);
        drawBorders();

        initialized=true;
    }

    double globalMaxToAnalise =0;
    int globalMaxIndex =0;
    int age=0;
    int timePos=0;

    public void clearMeasurement(){
        globalMaxToAnalise=0;
        globalMaxIndex=0;
        age=0;
        timePos=0;
    }

    public void plot(double[] toTransform) {
        double maxValue =0;
        int maxIndex = 0;

        paintSpectrumDisplay.setColor(Color.GREEN);

        float delta = ((float) width) / ((float) ( toTransform.length ));
        int center_of_the_graph = height/2;
        for (int i = 0; i < toTransform.length; i++) {
            float x = delta * i;
            double toAnalyze = toTransform[i];
            int downy = (int) (center_of_the_graph - (toAnalyze * 10));
            canvasDisplaySpectrum.drawLine(x, downy, x, center_of_the_graph, paintSpectrumDisplay);

            if(toAnalyze>maxValue){
                if(toAnalyze> globalMaxToAnalise){
                    globalMaxToAnalise = toAnalyze;
                    globalMaxIndex = i;
                }
                maxValue = toAnalyze;
                maxIndex = i;
            }
        }



        if(  maxValue > 1 ) {
            double convertFactor = 4000d / (double) (toTransform.length);
            int convertedIndex = (int)((double) maxIndex * convertFactor);
            int convertedGlobalMaxIndex = (int)((double) globalMaxIndex * convertFactor);

            String msg;

            msg = "Local: " + convertedIndex + " Hz /" + (int) maxValue
                    + " Max: " + convertedGlobalMaxIndex + " Hz / " + (int) globalMaxToAnalise;


            // Log.d(TAG, msg);
            if(output!=null){
                output.setText(msg);
            }
            if( age++ > maxAge ){
                int p = timePos;
                clearMeasurement();
                timePos=p;
            }

        }

        plotTimeInformation(toTransform);

        // invalidate();
    }

    int last_x=0;
    int last_y=0;

    private void plotTimeInformation(double[] toTransform) {
        // prints time pos
        int maxValue = toTransform.length;

        int x = timePos = ++timePos % width; // x
        int y = (int)( ((double) globalMaxIndex / (double) maxValue ) * (double) height);


        //canvasDisplaySpectrum.drawPoint(timePos, y , paintSpectrumDisplay);
        paintSpectrumDisplay.setColor(Color.BLUE);
        canvasDisplaySpectrum.drawLine(last_x, last_y, x, y , paintSpectrumDisplay);

        last_x = x;
        last_y = y;

    }


    private void init() {
        paintSpectrumDisplay = new Paint();
    }

    public void drawBorders() {
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

    // somehow things just worked after I overload the 3 constructors
    public TheSpectrumAnalyzerImageView(Context context, AttributeSet blah, int bleh) {
        super(context, blah, bleh);
        Log.d(TAG, "TheSpectrumAnalizerImageView3");
        init();
    }

    // somehow things just worked after I overload the 3 constructors
    public TheSpectrumAnalyzerImageView(Context context, AttributeSet blah) {
        super(context, blah);
        Log.d(TAG, "TheSpectrumAnalyzerImageView2");
        init();
    }

    // somehow things just worked after I overload the 3 constructors
    public TheSpectrumAnalyzerImageView(Context context) {
        super(context);
        Log.d(TAG, "TheSpectrumAnalyzerImageView1");
        init();
    }

}
