package com.example.bookyue.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.example.bookyue.R;

public class LabelImageView extends AppCompatImageView {

    private Paint textPaint;
    private Paint backgroundPaint;
    private Path pathText;
    private Path pathBackground;

    private int update;

    public LabelImageView(Context context) {
        super(context);
        init();
    }

    public LabelImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelImageView);
        update = typedArray.getInt(R.styleable.LabelImageView_update,0);
        typedArray.recycle();
        init();
    }

    public LabelImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelImageView);
        update = typedArray.getInt(R.styleable.LabelImageView_update,0);
        typedArray.recycle();
        init();
    }


    private void init() {
        pathText = new Path();
        pathBackground = new Path();

        textPaint = new Paint();
        textPaint.setFakeBoldText(true);
        textPaint.setColor(Color.WHITE);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.RED);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }

    public void setUpdate(int update){
        this.update = update;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (update == 1){
            //计算路径
            calculatePath(getMeasuredWidth());
            canvas.drawPath(pathBackground, backgroundPaint);
            int hOffset = (int) (getMeasuredWidth()/6.5);
            int vOffset = getMeasuredWidth()/23;
            int textSize = getMeasuredWidth()/10;
            textPaint.setTextSize(textSize);
            canvas.drawTextOnPath("New", pathText, hOffset, -vOffset, textPaint);
        }
    }

    /**
     * 计算路径              x1   x2  x3
     * ................................    distance（标签离右上角的垂直距离）
     * .                      .    .  .
     * .                        .    .. y1
     * .                          .   .
     * .                            . .
     * .                              . y2    height(标签垂直高度)
     * .                              .
     * ................................
     */
    private void calculatePath(int width) {

        int x2x3 = width/6;
        int x1x2 = width/5;

        float x1 = width-x1x2-x2x3;
        float x2 = width-x2x3;
        float y1 = x2x3;
        float y2 = x1x2+x2x3;

        pathText.reset();
        pathText.moveTo(x1,0);
        pathText.lineTo(width, y2);
        pathText.close();

        pathBackground.reset();
        pathBackground.moveTo(x1, 0);
        pathBackground.lineTo(x2, 0);
        pathBackground.lineTo(width, y1);
        pathBackground.lineTo(width, y2);
        pathBackground.close();
    }

}
