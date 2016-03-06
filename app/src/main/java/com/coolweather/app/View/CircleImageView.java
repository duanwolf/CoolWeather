package com.coolweather.app.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.coolweather.app.R;


/**
 * Created by duanbiwei on 2015/10/3.
 */
public class CircleImageView extends ImageView {
    private Paint paint = new Paint();


    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
      /*  TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        mBorderColor = a.getColor(R.styleable.CircleImageView_border_color, mBorderColor);
        final int def = (int)(2 * context.getResources().getDisplayMetrics().density + 0.5f);
        mBorderWidth = a.getDimensionPixelOffset(R.styleable.CircleImageView_border_width, def);
        a.recycle();*/
    }
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (null != drawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap b = toRoundCorner(bitmap, 14);
            final Rect rect = new Rect(0, 0, b.getWidth(), b.getHeight());
            paint.reset();
            canvas.drawBitmap(b, rect, rect, paint);
        } else {
            super.onDraw(canvas);
        }
    }

    private Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = bitmap.getWidth();
        canvas.drawCircle(x / 2, x / 2, x / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

  /*  private boolean useDefaultStyle = false;
    public void setUseDefaultStyle(boolean useDefaultStyle) {
        this.useDefaultStyle = useDefaultStyle;
    }

    @Override
    protected void onDraw(Canvas paramCancas) {
        if (useDefaultStyle) {
            super.onDraw(paramCancas);
            return;
        }

        final Drawable localDrawable = getDrawable();
        if (localDrawable == null) return;

        if (localDrawable instanceof NinePatchDrawable) return;

        if (paint == null) {
            final Paint localPaint = new Paint();
            localPaint.setFilterBitmap(false);
            localPaint.setXfermode(MASK_XFERMODE);
            localPaint.setAntiAlias(true);
            this.paint = localPaint;
        }
        final int width = getWidth();
        final int height = getHeight();

        int layer = paramCancas.saveLayer(0F, 0F, width, height, null);
        localDrawable.setBounds(0, 0, width, height);
        localDrawable.draw(paramCancas);
        if ((this.mask == null) || (this.mask.isRecycled())) {
            this.mask = createOvalBitmap(width, height);
        }
        paramCancas.drawBitmap(this.mask, 0F, 0F, paint);
        paramCancas.restoreToCount(layer);
        drawBorder(paramCancas, width, height);
    }

    public Bitmap createOvalBitmap(final int width, final int height) {
        Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
        Bitmap localBitmap = Bitmap.createBitmap(width, height, localConfig);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        final int padding = mBorderWidth - 3;
        RectF localRectF = new RectF(padding, padding, width - padding, height - padding);
        localCanvas.drawOval(localRectF, localPaint);
        return localBitmap;
    }

    private void drawBorder(Canvas canvas, final int width, final int height) {
        if (mBorderWidth == 0) {
            return;
        }
        final Paint mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        canvas.drawCircle(width >> 1, height >> 1, (width - mBorderWidth) >> 1, mBorderPaint);
        canvas = null;
    } */
}
