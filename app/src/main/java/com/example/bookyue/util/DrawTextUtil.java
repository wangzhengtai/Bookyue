package com.example.bookyue.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class DrawTextUtil {

    private static volatile DrawTextUtil mInstance;
    private Paint mBigTitlePaint;                       //画粗标题的画笔
    private Paint mSmallTitlePaint;                     //画小标题的画笔
    private Paint mTextPaint;                           //画正文的画笔

    private int mAvailableWidth;                       //可用宽度，即字体能绘制的区域宽度   单位为px
    //private int mAvailableHeight;                      //可用高度，即字体能绘制的区域高度   单位为px
    private static final int HorizontalPadding = 20;         //上下padding  单位为dp
    private static final int VerticalPadding = 20;           //左右padding  单位为dp

    private int mX;                                    //字符绘制时X的坐标  单位为px
    private int mY;                                    //字符绘制时Y的坐标  单位为px
    private int mLastX;                                //屏幕中最靠右的宽度
    private int mLastY;                                //屏幕中字体最低能绘制的高度
    private float mIndentSize;                         //首行缩进的宽度

    private float mTimePageY;                            //绘制时间和页面下标时Y的位置

    private float mLineSpace = 1.5f;                   //行间距

    //构造器私有
    private DrawTextUtil(Context context){
        mBigTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);       //画粗标题的画笔
        mBigTitlePaint.setColor(Color.BLACK);
        mBigTitlePaint.setFakeBoldText(true);
        mBigTitlePaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "楷体.ttf"));
        mBigTitlePaint.setTextSize(DensityUtil.dp2px(context,30));

        mSmallTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);     //画小标题的画笔
        mSmallTitlePaint.setColor(Color.GRAY);
        mSmallTitlePaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "楷体.ttf"));
        mSmallTitlePaint.setTextSize(DensityUtil.dp2px(context,10));

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);               //画正文的画笔
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "楷体.ttf"));
        mTextPaint.setTextSize(DensityUtil.dp2px(context,18));

        //手机屏幕宽度   单位为px
        int widthScreen = context.getResources().getDisplayMetrics().widthPixels;
        //手机屏幕高度   单位为px
        int heightScreen = context.getResources().getDisplayMetrics().heightPixels;
        mAvailableWidth = widthScreen - 2*DensityUtil.dp2px(context,HorizontalPadding);
        //mAvailableHeight = heightScreen - 2*DensityUtil.dp2px(context,VerticalPadding);
        mX = DensityUtil.dp2px(context,HorizontalPadding);    //初始时，X的坐标为左内边距的大小
        mY = DensityUtil.dp2px(context,VerticalPadding);      //初始时，Y的坐标为上内边距的大小
        mLastX = widthScreen - DensityUtil.dp2px(context,HorizontalPadding);
        mLastY = heightScreen - DensityUtil.dp2px(context,VerticalPadding+5);     //多加5dp的内间距

        mTimePageY = heightScreen - DensityUtil.dp2px(context,VerticalPadding) + mSmallTitlePaint.getFontSpacing();

        mIndentSize = mTextPaint.measureText("你好");      //测量“你好”两字的宽度,并将其作为缩进的宽度
    }

    //单例，这样每个view绘制时就不会重复创建了
    public static DrawTextUtil getInstance(Context context){
        if (mInstance == null){
            synchronized (DrawTextUtil.class){
                if (mInstance == null){
                    mInstance = new DrawTextUtil(context);
                }
            }
        }
        return mInstance;
    }


    public Paint getBigTitlePaint(){
        return mBigTitlePaint;
    }

    public Paint getSmallTitlePaint(){
        return mSmallTitlePaint;
    }

    public Paint getTextPaint(){
        return mTextPaint;
    }


    public int getAvailableWidth() {
        return mAvailableWidth;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    public int getLastX() {
        return mLastX;
    }

    public int getLastY() {
        return mLastY;
    }

    public float getTimePageY() {
        return mTimePageY;
    }

    public float getIndentSize() {
        return mIndentSize;
    }

    public float getLineSpace() {
        return mLineSpace;
    }

    public void setLineSpace(float lineSpace) {
        mLineSpace = lineSpace;
    }

    //用于判断一个字符是否是中文标点
    public static boolean isChinesePunctuation(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || ub == Character.UnicodeBlock.VERTICAL_FORMS;
    }
}
