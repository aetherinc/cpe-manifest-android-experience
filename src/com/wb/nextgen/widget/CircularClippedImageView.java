package com.wb.nextgen.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.wb.nextgen.R;

/**
 * Created by gzcheng on 3/3/16.
 */
public class CircularClippedImageView extends ImageView{

    public CircularClippedImageView(Context context) {
        super(context);
    }

    public CircularClippedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularClippedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircularClippedImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public void setImageDrawable(Drawable drawable){
        Drawable d = drawable;
        if (drawable != null) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap clipped = circularClipBitmap(bitmap);
            d = new BitmapDrawable(getResources(), clipped);
        }
        super.setImageDrawable(d);
    }
    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm != null)
            super.setImageBitmap(circularClipBitmap(bm) );

        super.setImageBitmap(bm);
    }

    public void setActivated(boolean bool){
        super.setActivated(bool);
        dispatchDraw(new Canvas());
    }


    private Bitmap circularClipBitmap(Bitmap bm){
        Bitmap output = Bitmap.createBitmap(bm.getWidth(),
                bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bm.getWidth(), bm.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        float centerX = bm.getWidth() / 2;
        float centerY = bm.getHeight() / 2;
        float radius = Math.min(centerX, centerY);
        float insideRaius = radius - getResources().getDimensionPixelSize(R.dimen.selected_thumbnail_border_size);

        canvas.drawCircle(centerX, centerY, insideRaius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bm, rect, rect, paint);
        if (isActivated()) {

            //create another bitmap for selected overlay
            Bitmap overlay = Bitmap.createBitmap(getWidth(),
                    getHeight(), Bitmap.Config.ARGB_8888);
            Canvas onCanvas = new Canvas(overlay);

            // draw a circle on the overlay bitmap
            Paint selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            selectedPaint.setColor(getResources().getColor(android.R.color.white));
            onCanvas.drawCircle(centerX, centerY, radius, selectedPaint);

            selectedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            selectedPaint.setColor(getResources().getColor(android.R.color.black));
            onCanvas.drawCircle(centerX, centerY, insideRaius, selectedPaint);

            // draw on top of normal state image
            selectedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
            canvas.drawBitmap(overlay, rect, rect, selectedPaint);
        }
        return output;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

}
