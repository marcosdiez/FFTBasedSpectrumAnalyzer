package com.somitsolutions.android.spectrumanalyzer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

/**
 * Created by Marcos on 09-Mar-15.
 */
public class TheScaleImageView extends ImageView {
    Paint paintScaleDisplay;
    Bitmap bitmapScale;
    Canvas canvasScale;
    //Bitmap scaled;
    public TheScaleImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        // if(width >512){
        int width = this.getWidth();
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
        int width = this.getWidth();
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
