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

    public CalculateStatistics statistics = new CalculateStatistics();

    public final int maxAge = 100;
    int height = 0;
    int width = 0;
    boolean initialized = false;

    public static String TAG = "TheSpectrumAnalyzerImageView";

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

    public String msg="";






    public void plot(double[] toTransform) {
        paintSpectrumDisplay.setColor(Color.GREEN);

        float delta = ((float) width) / ((float) ( toTransform.length ));
        int center_of_the_graph = height/2;

        statistics.beforeIteration();

        for (int i = 0; i < toTransform.length; i++) {
            float x = delta * i;
            double toAnalyze = toTransform[i];
            int downy = (int) (center_of_the_graph - (toAnalyze * 10));
            statistics.analyzeElement(i, toAnalyze);
            canvasDisplaySpectrum.drawLine(x, downy, x, center_of_the_graph, paintSpectrumDisplay);
        }

        statistics.afterIteration();
        writeMsg(toTransform.length);
    }

    private void writeMsg(int tl){
        double convertFactor = 4000d / (double) (tl);
        int convertedIndex = (int)((double) statistics.getLargestX()  * convertFactor);
        msg = "Local: " + convertedIndex + " Hz " + statistics.getLargestY();
        if(statistics.getLargestY() > .01 ) {
            Log.d(TAG, msg);
        }
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
