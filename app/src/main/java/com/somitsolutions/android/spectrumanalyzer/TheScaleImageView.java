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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //getMeasuredHeight() and getMeasuredWidth() now contain the suggested size
        width = this.getWidth(); //  widthMeasureSpec;
        height = this.getHeight();
        if(width == 0) {
            return;
        }

        Log.d("MMM", "TheScaleImageView.onMeasure: " + width + "/" + height);
        bitmapScale = Bitmap.createBitmap(width, (int) 50, Bitmap.Config.ARGB_8888);
        paintScaleDisplay = new Paint();
        paintScaleDisplay.setColor(Color.WHITE);
        paintScaleDisplay.setStyle(Paint.Style.FILL);
        canvasScale = new Canvas(bitmapScale);
        setImageBitmap(bitmapScale);
    }

    //Bitmap scaled;
    public TheScaleImageView(Context context, AttributeSet blah , int bleh) {
        super(context, blah, bleh);
        Log.d("MMM", "TheScaleImageViewZ");
    }

    public TheScaleImageView(Context context, AttributeSet blah) {
        super(context, blah);
        Log.d("MMM", "TheScaleImageViewY");
    }

    public TheScaleImageView(Context context) {
        super(context);
        Log.d("MMM", "TheScaleImageViewX");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if(width == 0 ) {
            return;
        }
        Log.d("MMM", "TheScaleImageView.onDraw: " + width + "/" + height);
        // int x_Of_BimapScale = bitmapScale.

//        //if(width > 512){
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
        //}
        //canvas.drawBitmap(bitmapScale, 0, 400, paintScaleDisplay);
        invalidate();
    }

}
