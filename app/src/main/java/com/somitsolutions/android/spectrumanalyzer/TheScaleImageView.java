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
public class TheScaleImageView extends ImageView {
    Paint paintScaleDisplay;
    Bitmap bitmapScale;
    Canvas canvasScale;
    int width = 0;
    int height = 0;
    boolean initialized = false;

    public static String TAG = "TheScaleImageView";

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //getMeasuredHeight() and getMeasuredWidth() now contain the suggested size
        if(initialized){
            return;
        }

        width = this.getWidth();
        height = this.getHeight();
        if(width == 0) {
            return;
        }

        bitmapScale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        setImageBitmap(bitmapScale);
        canvasScale = new Canvas(bitmapScale);
        plot();
        initialized=true;
    }

    void init(){
        paintScaleDisplay = new Paint();
        paintScaleDisplay.setColor(Color.WHITE);
        paintScaleDisplay.setStyle(Paint.Style.FILL);
    }

    // somehow things just worked after I overload the 3 constructors
    public TheScaleImageView(Context context, AttributeSet blah , int bleh) {
        super(context, blah, bleh);
        Log.d(TAG, "TheScaleImageViewZ");
        init();
    }

    // somehow things just worked after I overload the 3 constructors
    public TheScaleImageView(Context context, AttributeSet blah) {
        super(context, blah);
        Log.d(TAG, "TheScaleImageViewY");
        init();
    }

    // somehow things just worked after I overload the 3 constructors
    public TheScaleImageView(Context context) {
        super(context);
        Log.d(TAG, "TheScaleImageViewX");
        init();
    }

    private void plot(){
        Log.d(TAG, "plot()");
        float delta = width / 4;
        float delta_by_10 = delta / 10;

        int lineHeight = height * 2/5;
        canvasScale.drawColor(Color.BLUE);
        canvasScale.drawLine(0, lineHeight, width, lineHeight, paintScaleDisplay);
        for (float i = 0, j = 0; i < width; i = i + delta, j++) {
            for (float k = i; k < (i + delta); k = k + delta_by_10) {
                canvasScale.drawLine(k, lineHeight, k, lineHeight*2/5, paintScaleDisplay);
            }
            canvasScale.drawLine(i, height*4/5, i, 0, paintScaleDisplay);
            String text = Integer.toString((int) j) + " KHz";
            canvasScale.drawText(text, i, height*45/50, paintScaleDisplay);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if(bitmapScale == null){return;}
        canvas.drawBitmap(bitmapScale, 0, 0, paintScaleDisplay);
    }

}
