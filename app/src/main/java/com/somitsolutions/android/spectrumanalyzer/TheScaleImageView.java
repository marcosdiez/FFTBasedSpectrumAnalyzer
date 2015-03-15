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
    boolean initialized = false;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //getMeasuredHeight() and getMeasuredWidth() now contain the suggested size
        if(initialized){
            return;
        }

        width = this.getWidth(); //  widthMeasureSpec;
        if(width == 0) {
            return;
        }


        bitmapScale = Bitmap.createBitmap(width, (int) 50, Bitmap.Config.ARGB_8888);
        setImageBitmap(bitmapScale);
        canvasScale = new Canvas(bitmapScale);
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
        Log.d("MMM", "TheScaleImageViewZ");
        init();
    }

    // somehow things just worked after I overload the 3 constructors
    public TheScaleImageView(Context context, AttributeSet blah) {
        super(context, blah);
        Log.d("MMM", "TheScaleImageViewY");
        init();
    }

    // somehow things just worked after I overload the 3 constructors
    public TheScaleImageView(Context context) {
        super(context);
        Log.d("MMM", "TheScaleImageViewX");
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if(!initialized) {
            return;
        }

        float delta = width / 4;
        float delta_by_8 = delta / 8;

        canvasScale.drawLine(0, 30, width, 30, paintScaleDisplay);
        for (float i = 0, j = 0; i < width; i = i + delta, j++) {
            for (float k = i; k < (i + delta); k = k + delta_by_8) {
                canvasScale.drawLine(k, 30, k, 25, paintScaleDisplay);
            }
            canvasScale.drawLine(i, 40, i, 25, paintScaleDisplay);
            String text = Integer.toString((int) j) + " KHz";
            canvasScale.drawText(text, i, 45, paintScaleDisplay);
        }
        canvas.drawBitmap(bitmapScale, 0, 0, paintScaleDisplay);

        invalidate();
    }

}
