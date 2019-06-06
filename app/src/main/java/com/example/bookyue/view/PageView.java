package com.example.bookyue.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.example.bookyue.util.DateUtil;
import com.example.bookyue.util.DensityUtil;
import com.example.bookyue.util.DrawTextUtil;


public class PageView extends View {

    private static final String TAG = "PageView";

    private DrawTextUtil mDrawTextUtil;          //辅助绘制的工具类，其中存储着画笔实例和绘制相关数据
    private Paint mBigTitlePaint;
    private Paint mSmallTitlePaint;
    private Paint mTextPaint;
    private int mAvailableWidth;                       //可用宽度，即字体能绘制的区域宽度   单位为px
    private int mX;                                    //字符绘制时X的坐标  单位为px
    private int mY;                                    //字符绘制时Y的坐标  单位为px
    private int mLastX;                                //屏幕中所允许到达的最右边界
    private int mLastY;                                //屏幕中字体最低能绘制的高度
    private float mIndentSize;                         //首行缩进的宽度
    private float mLineSpace;                          //行间距大小
    private float[] mMeasuredWidth = {0};              //存储Paint.breakText()测量的宽度
    private boolean mFirst;                            //标识是否是首行

    //需传参过来的变量
    private String mTitle;              //章节标题
    private String[] mParagraphs;       //章节体  即多个段
    private int mStartTitle;            //章节标题某一行绘制时的字符起始下标
    private int mEndTitle;              //章节标题某一行绘制时的字符终止下标
    private int mStartParagraph;        //章节体中某段某一行绘制时的字符起始下标
    private int mEndParagraph;          //章节体中某段某一行绘制时的字符终止下标
    private int mIndexOfParagraphs;      //章节体段落下标
    private int mPages;                 //存储当前章节的总页数
    private int mIndexOfPages;          //存储当前章节页数的下标

    Paint mPaint;

    public PageView(Context context) {
        super(context);
        init(context);
    }

    public PageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mDrawTextUtil = DrawTextUtil.getInstance(context);
        mBigTitlePaint = mDrawTextUtil.getBigTitlePaint();
        mSmallTitlePaint = mDrawTextUtil.getSmallTitlePaint();
        mTextPaint = mDrawTextUtil.getTextPaint();
        mLastX = mDrawTextUtil.getLastX();
        mLastY = mDrawTextUtil.getLastY();
        mIndentSize = mDrawTextUtil.getIndentSize();
        mLineSpace = mDrawTextUtil.getLineSpace();

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
    }


    public void setTitle(String title){
        Log.i(TAG, "setTitle: 章节标题传过来了");
        mTitle = title;
    }

    public void setParagraphs(String[] paragraphs){
        Log.i(TAG, "setParagraphs: 章节体内容传过来了");
        mParagraphs = paragraphs;
    }

    public void setPages(int pages){
        mPages = pages;
    }

    public void setIndexOfPages(int indexOfPages){
        mIndexOfPages = indexOfPages+1;
    }

    public void setIndexOfParagraphs(int indexOfParagraphs){
        mIndexOfParagraphs = indexOfParagraphs;
    }

    public void setStartParagraph(int startParagraph){
        mStartParagraph = startParagraph;
        mEndParagraph = startParagraph;
    }


    //因初始值变化，导致其绘制不出来任何东西
    @Override
    protected void onDraw(Canvas canvas) {
        mAvailableWidth = mDrawTextUtil.getAvailableWidth();
        mX = mDrawTextUtil.getX();                        //最初的X坐标
        mY = mDrawTextUtil.getY();                        //最初的Y坐标
        int startParagraph = mStartParagraph;
        int endParagraph = mEndParagraph;
        mFirst = false;


        //canvas.drawRect(mX,mY,mX+mAvailableWidth,mLastY,mPaint);


        Log.i(TAG, "onDraw: ------------------------------------------------------------------");
        Log.i(TAG, "onDraw: 绘制步骤开始了");
        Log.i(TAG, "onDraw: Title = "+mTitle);
        Log.i(TAG, "onDraw: 第"+mIndexOfPages+"页的绘制--------------------------");
        Log.i(TAG, "onDraw: 第"+mIndexOfParagraphs+"段");
        Log.i(TAG, "onDraw: startParagraph = "+startParagraph);
        Log.i(TAG, "onDraw: 当前的Y值 = "+mY);
        Log.i(TAG, "onDraw: 可用宽度 "+mAvailableWidth);
        Log.i(TAG, "onDraw: 所允许绘制的最低的mLastY = "+mLastY);
        Log.i(TAG, "onDraw: ------------------------------------------------------------------");
        if (mTitle != null){        //章节标题不为null时绘制章节标题
            if (mIndexOfPages == 1){        //章节首页
                mY += DensityUtil.dp2px(getContext(),20);        //下移20dp
                while (mEndTitle<mTitle.length()){
                    mEndTitle += mBigTitlePaint.breakText(mTitle,mStartTitle,mTitle.length(),true,
                            mAvailableWidth,mMeasuredWidth);
                    mY += mBigTitlePaint.getFontSpacing();
                    canvas.drawText(mTitle,mStartTitle,mEndTitle,mX,mY,mBigTitlePaint);
                    mStartTitle = mEndTitle;
                }
                mY += DensityUtil.dp2px(getContext(),80);       //首页标题下空出100dp间距
            }else{
                while (mEndTitle<mTitle.length()){
                    mEndTitle += mSmallTitlePaint.breakText(mTitle,mStartTitle,mTitle.length(),true,
                            mAvailableWidth,mMeasuredWidth);
                    mY += mSmallTitlePaint.getFontSpacing();
                    canvas.drawText(mTitle,mStartTitle,mEndTitle,mX,mY,mSmallTitlePaint);
                    mStartTitle = mEndTitle;
                }
                //mY += DensityUtil.dp2px(getContext(),5);     //下移5dp
            }
            //重置为0
            mStartTitle = 0;
            mEndTitle = 0;
        }
        //开始绘制章节体内容
        for (int index = mIndexOfParagraphs;index<mParagraphs.length;index++){      //段落的循环
            //之所以样判断是因为当前页可能是紧接前一页的内容，并非是一段的起始处
            if (startParagraph == 0){         //段起始处  缩进两字符宽度
                mFirst = true;                 //标识为首行
                mX += mIndentSize;             //缩进两字符
                mAvailableWidth -= mIndentSize;    //字符绘制的可用宽度也应相应减少
            }
            while (endParagraph<mParagraphs[index].length()){   //段中行的循环
                mY += mTextPaint.getFontSpacing()*mLineSpace;
                if (mY > mLastY){    //将要绘制的下一行的字符的Y超过了mLaseY，则不允许绘制了，即到当前页的底部了
                    //画当前时间
                    canvas.drawText(DateUtil.getSystemTime(),mDrawTextUtil.getX(),
                            mDrawTextUtil.getTimePageY(),mSmallTitlePaint);
                    //画当前页面数与页面总数
                    String pageStr = mIndexOfPages+"/"+mPages;
                    canvas.drawText(pageStr,mLastX-mSmallTitlePaint.measureText(pageStr),
                            mDrawTextUtil.getTimePageY(),mSmallTitlePaint);
                    return;
                }
//                if (mLastY - mY > mTextPaint.getFontSpacing()){
//                    Log.i(TAG, "onDraw: mLastY - mY = "+(mLastY - mY));
//                    endParagraph += mTextPaint.breakText(mParagraphs[index],startParagraph,mParagraphs[index].length(),
//                            true, mAvailableWidth,mMeasuredWidth);
//                    Log.i(TAG, "onDraw: "+mParagraphs[index].substring(startParagraph,endParagraph));
//                    canvas.drawText(mParagraphs[index],startParagraph,endParagraph,mX,mY,mTextPaint);
//                    startParagraph = endParagraph;
//                }

                Log.i(TAG, "onDraw: mLastY - mY = "+(mLastY - mY));
                endParagraph += mTextPaint.breakText(mParagraphs[index],startParagraph,mParagraphs[index].length(),
                        true, mAvailableWidth,mMeasuredWidth);
                Log.i(TAG, "onDraw: "+mParagraphs[index].substring(startParagraph,endParagraph));
                //只要是标点符号，就强加给上一行末尾
                while (endParagraph < mParagraphs[index].length() &&
                        DrawTextUtil.isChinesePunctuation(mParagraphs[index].charAt(endParagraph))){
                    endParagraph++;
                }
                canvas.drawText(mParagraphs[index],startParagraph,endParagraph,mX,mY,mTextPaint);
                startParagraph = endParagraph;
                if (mFirst){           //当前首行绘制完成后，将缩进取消
                    mX -= mIndentSize;
                    mAvailableWidth += mIndentSize;
                    mFirst = false;
                }
            }
            startParagraph = 0;           //一段绘制完成后，将段开始绘制下标重置为0
            endParagraph = 0;             //结束下标也重置为0
            mY += mTextPaint.getFontSpacing()*0.5;           //下移一行的间距
        }
        //最后一页的绘制会执行到这
        //画当前时间
        canvas.drawText(DateUtil.getSystemTime(),mDrawTextUtil.getX(),
                mDrawTextUtil.getTimePageY(),mSmallTitlePaint);
        String pageStr = mIndexOfPages+"/"+mPages;
        canvas.drawText(pageStr,mLastX-mSmallTitlePaint.measureText(pageStr),
                mDrawTextUtil.getTimePageY(),mSmallTitlePaint);






//        for (int i=mIndexOfParagraphs;i<mParagraphs.length;i++){    //段的循环
//            if (i == 0){                      //第0段的肯定是在首页，需要绘制粗标题
//                Log.i(TAG, "calculatePages: 0 0");
//
//                mY += DensityUtil.dp2px(getContext(),20);       //下移20dp
//                while (mEndTitle<mTitle.length()){
//                    mEndTitle += mBigTitlePaint.breakText(mTitle,mStartTitle,mTitle.length(),true,
//                            mAvailableWidth,mMeasuredWidth);
//                    mY += mBigTitlePaint.getFontSpacing();
//                    mStartTitle = mEndTitle;
//                }
//                mY += DensityUtil.dp2px(getContext(),100);          //首页标题下空出100dp间距
//                //重置 startTitle和endTitle
//                mStartTitle = 0;
//                mEndTitle = 0;
//            }
//
//            if (startParagraph == 0){         //段起始处  缩进两字符宽度
//                mFirst = true;                 //标识为首行
//                mX += mIndentSize;             //缩进两字符
//                mAvailableWidth -= mIndentSize;    //字符绘制的可用宽度也应相应减少
//            }
//
//            while (endParagraph<mParagraphs[i].length()){      //段中行的循环
//                Log.i(TAG, "calculatePages: mY = "+mY);
//                mY += mTextPaint.getFontSpacing();
//                Log.i(TAG, "calculatePages: mY += mTextPaint.getFontSpacing() "+mY);
//                if (mY > mLastY){    //将要绘制的下一行的字符的mY超过了lasemY，则不允许绘制了，即到当前页的底部了
//                    //一页已经绘制完成
//                    Pair<Integer,Integer> pair = new Pair<>(i,endParagraph);
//                    Log.i(TAG, "calculatePages: mLastY = "+mLastY);
//                    Log.i(TAG, "calculatePages: "+i+" "+endParagraph);
//
//                    //重置mY的高度  变为初始值
//                    mY = mDrawTextUtil.getY();
//
//                    //可能正是一段的起始处
//                    if (mFirst){    //减去缩进
//                        mX -= mIndentSize;
//                        mAvailableWidth += mIndentSize;
//                    }
//                    //绘制一页开头的小标题
//                    while (mEndTitle<mTitle.length()){
//                        mEndTitle += mSmallTitlePaint.breakText(mTitle,mStartTitle,mTitle.length(),
//                                true, mAvailableWidth,mMeasuredWidth);
//                        mY += mSmallTitlePaint.getFontSpacing();
//                        mStartTitle = mEndTitle;
//                    }
//                    mY += DensityUtil.dp2px(getContext(),20);     //下移20dp
//                    //重置 startTitle和endTitle
//                    mStartTitle = 0;
//                    mEndTitle = 0;
//
//                    if (mFirst){   //恢复缩进
//                        mX += mIndentSize;              //缩进两字符
//                        mAvailableWidth -= mIndentSize;
//                    }
//                }
//
//                Log.i(TAG, "calculatePages: mLastY - mY = "+(mLastY - mY));
//                endParagraph += mTextPaint.breakText(mParagraphs[i],startParagraph,mParagraphs[i].length(),
//                        true, mAvailableWidth,mMeasuredWidth);
//                Log.i(TAG, "calculatePages: "+mParagraphs[i].substring(startParagraph,endParagraph));
//                startParagraph = endParagraph;
//                if (mFirst){           //当前首行绘制完成后，将缩进取消
//                    mX -= mIndentSize;
//                    mAvailableWidth += mIndentSize;
//                    mFirst = false;
//                }
//            }
//            //一段绘制完成
//            startParagraph = 0;           //一段绘制完成后，将段开始绘制下标重置为0
//            endParagraph = 0;             //结束下标也重置为0
//            mY += mTextPaint.getFontSpacing();            //下移一行的间距
//        }
    }
}
