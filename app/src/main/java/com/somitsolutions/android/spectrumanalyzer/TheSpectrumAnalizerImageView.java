package com.somitsolutions.android.spectrumanalyzer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Marcos on 09-Mar-15.
 */
public class TheSpectrumAnalizerImageView extends ImageView {
    public Bitmap bitmapDisplaySpectrum=null;
    public Canvas canvasDisplaySpectrum=null;
    public Paint paintSpectrumDisplay=null;

    int height = 0;
    int width = 0;
    boolean initialized = false;

    public static String TAG = "TheSpectrumAnalizerImageView";

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

    public void plot(double[] toTransform) {
        double maxValue =0;
        int maxIndex = 0;


        paintSpectrumDisplay.setColor(Color.GREEN);

        float delta = ((float) width) / ((float) ( toTransform.length -1 ));
        for (int i = 0; i < toTransform.length; i++) {
            float x = delta * i;
            double toAnalyze = toTransform[i];
            int downy = (int) (height/2 - (toAnalyze * 10));
            int upy = height/2;
            canvasDisplaySpectrum.drawLine(x, downy, x, upy, paintSpectrumDisplay);

            if(toAnalyze>maxValue){
                maxValue=toAnalyze;
                maxIndex = i;
            }
        }

        int fixedValue =(int)maxValue*1000;

        if( fixedValue > 0 ) {
            Log.d(TAG, "Calc:" +  width + "/"
                    + height + "/" +
                    toTransform.length + "/" + maxIndex + "/" + fixedValue);
        }
        invalidate();
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
    public TheSpectrumAnalizerImageView(Context context, AttributeSet blah, int bleh) {
        super(context, blah, bleh);
        Log.d(TAG, "TheSpectrumAnalizerImageView3");
        init();
    }

    // somehow things just worked after I overload the 3 constructors
    public TheSpectrumAnalizerImageView(Context context, AttributeSet blah) {
        super(context, blah);
        Log.d(TAG, "TheSpectrumAnalizerImageView2");
        init();
    }

    // somehow things just worked after I overload the 3 constructors
    public TheSpectrumAnalizerImageView(Context context) {
        super(context);
        Log.d(TAG, "TheSpectrumAnalizerImageView1");
        init();
    }

}
