package com.example.bookyue.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


public class ReadView extends ViewGroup {

    private static final String TAG = "ReadView";

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;          //速度跟踪
    private int mTouchSlop;          //判定为拖动的最小移动像素
    private int mWidthScreen;         //屏幕宽度
    private int mHeightScreen;        //屏幕高度
    private int mXDown;            //手指刚放下去时的x值
    private int mXLast;            //上一次x的值
    private int mOrientation;       //页面布局方向  水平 竖直
    private int mDirection = MOVE_NO_RESULT;         //页面滚动方向  向左向右  向上向下
    private int mTouchResult = MOVE_NO_RESULT;         //最终触摸结果
    private int mArea;              //点击事件发生的区域   left center right
    private int mTouchMode = TOUCH_MODE_CLICK;         //触摸模式，滚动、快速滚动、点击  默认是点击
    private View mScrollView;
    private View mPrePageView;
    private View mCurPageView;
    private View mNextPageView;
    private OnBookStateListener mBookStateListener;           //书籍状态监听器
    private OnTouchEventListener mTouchEventListener;         //触摸事件监听器

    private static final int MOVE_NO_RESULT = -1;
    private static final int MOVE_TO_LEFT = 0;
    private static final int MOVE_TO_RIGHT = 1;
    private static final int MOVE_TO_TOP = 2;
    private static final int MOVE_TO_BOTTOM = 3;

    private static final int TOUCH_MODE_SCROLL = -1;
    private static final int TOUCH_MODE_FAST_SCROLLING = 0;
    private static final int TOUCH_MODE_CLICK = 1;

    private static final int LEFT_AREA = -1;
    private static final int CENTER_AREA = 0;
    private static final int RIGHT_AREA = 1;


    public ReadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        // 获取TouchSlop值
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        mVelocityTracker = VelocityTracker.obtain();
        mWidthScreen = context.getResources().getDisplayMetrics().widthPixels;
        mHeightScreen = context.getResources().getDisplayMetrics().heightPixels;
        Log.i(TAG, "ReadView: mWidthScreen="+mWidthScreen+" mHeightScreen="+mHeightScreen);

        //initView(context);
    }


    public void initView(View prePageView,View curPageView,View nextPageView){

        mPrePageView = prePageView;
        mCurPageView = curPageView;
        mNextPageView = nextPageView;

        //实例化view
//        mPrePageView = LayoutInflater.from(context).inflate(R.layout.text_page,null,false);
//        mCurPageView = LayoutInflater.from(context).inflate(R.layout.text_page,null,false);
//        mNextPageView = LayoutInflater.from(context).inflate(R.layout.text_page,null,false);
//        ((TextView)mPrePageView.findViewById(R.id.text_page)).setText(String.valueOf(1));
//        ((TextView)mCurPageView.findViewById(R.id.text_page)).setText(String.valueOf(2));
//        ((TextView)mNextPageView.findViewById(R.id.text_page)).setText(String.valueOf(3));

        //动态添加view
        addView(mNextPageView);
        addView(mCurPageView);
        addView(mPrePageView);

        //将页面向左滚动，展示出curPageView
        mPrePageView.scrollTo(mWidthScreen,0);
    }

    //直接替换      或者暴露内部view对象，重新绘制？？？？？？？？
    public void refreshView(View prePageView,View curPageView,View nextPageView){
        //移除原先的view
        removeView(mPrePageView);
        removeView(mCurPageView);
        removeView(mNextPageView);

        mPrePageView = prePageView;
        mCurPageView = curPageView;
        mNextPageView = nextPageView;

        //添加新的view
        addView(mNextPageView);
        addView(mCurPageView);
        addView(mPrePageView);

        //将页面向左滚动，展示出curPageView
        mPrePageView.scrollTo(mWidthScreen,0);
    }

//    //初始化页面的文本
//    public void initText(String curText,String nextText){
//        //((TextView)mPrePageView.findViewById(R.id.text_page)).setText(String.valueOf(1));
//        ((TextView)mCurPageView.findViewById(R.id.text_page)).setText(curText);
//        ((TextView)mNextPageView.findViewById(R.id.text_page)).setText(nextText);
//    }

    public void setOnBookStateListener(OnBookStateListener onBookStateListener){
        mBookStateListener = onBookStateListener;
    }

    public void setOnTouchEventListener(OnTouchEventListener onTouchEventListener){
        mTouchEventListener = onTouchEventListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "onMeasure: 子元素的数量："+getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            // 为ScrollerLayout中的每一个子控件测量大小
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "onLayout: ");
//        if (changed){
//            Log.i(TAG, "onLayout: changed");
//            for (int i = 0; i < getChildCount(); i++) {
//                View childView = getChildAt(i);
//                //覆盖样式布局
//                childView.layout(0,0,
//                        childView.getMeasuredWidth(), childView.getMeasuredHeight());
//            }
//        }
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            //覆盖样式布局
            childView.layout(0,0,
                    childView.getMeasuredWidth(), childView.getMeasuredHeight());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()){
                    Log.i(TAG, "onInterceptTouchEvent: mScroller.isFinished()-------------------");
                    mScroller.abortAnimation();
                    //mScroller.forceFinished(true);
                    break;
                }
                //确定点击事件发生的位置
                if (x < mWidthScreen/3)
                    mArea = LEFT_AREA;
                else if (x > 2*mWidthScreen/3)
                    mArea = RIGHT_AREA;
                else
                    mArea = CENTER_AREA;
                //记录下up事件时x的值
                mXDown = x;
                Log.i(TAG, "onInterceptTouchEvent--------------------DOWN--------------------: mXDown"
                        +mXDown+"\n");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onInterceptTouchEvent---------------------MOVE---------------------\n");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onInterceptTouchEvent----------------------UP----------------------\n");
                break;
        }
        Log.i(TAG, "onInterceptTouchEvent----------------------------END-----------------------------------\n");
        return true;                  //拦截全部事件
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x = (int) event.getX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouchEvent: -------------------DOWN---------------------\n");
                if (!mScroller.isFinished()) {
                    Log.i(TAG, "onTouchEvent: mScroller.abortAnimation();");
                    mScroller.abortAnimation();
                    //mScroller.forceFinished(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:               //这里处理手指触摸时的滚动事件
                Log.i(TAG, "onTouchEvent: --------------------MOVE---------------------\n");
                if (!mScroller.isFinished()){
                    Log.i(TAG, "onTouchEvent: mScroller.abortAnimation();");
                    mScroller.abortAnimation();
                    //mScroller.forceFinished(true);
                    break;
                }
                //距离是需要时时更新的
                int distance = mXDown - x;               //起始点减去终止点
                Log.i(TAG, "onTouchEvent:       x:"+x+"     mXLast:"+mXDown);
                Log.i(TAG, "onTouchEvent:       distance:"+distance);
                //因为一次触摸事件中，就会有好多的move事件产生，在第一次达到要求的move事件中设置值，其后的
                //move事件，无需在重复判断，只需执行相应的操作即可
                if (mBookStateListener.hasPrePage()){
                    Log.i(TAG, "onTouchEvent: 还有上一页");
                }else{
                    Log.i(TAG, "onTouchEvent: 没有上一页了");
                }
                if (mBookStateListener.hasNextPage()){
                    Log.i(TAG, "onTouchEvent: 还有下一页");
                }else{
                    Log.i(TAG, "onTouchEvent: 没有下一页了");
                }
                if (mDirection == MOVE_NO_RESULT){
                    if (mBookStateListener.hasNextPage() && distance > 0) {
                        mDirection = MOVE_TO_LEFT;
                        //mTouchResult = MOVE_TO_LEFT;//由于触摸时会不断滚动，所以不能在这设置
                    } else if (mBookStateListener.hasPrePage() && distance < 0) {
                        mDirection = MOVE_TO_RIGHT;
                        //mTouchResult = MOVE_TO_RIGHT;
                    }
                }
                //初始时判断一下即可,同时设置滚动的view
                if (mTouchMode == TOUCH_MODE_CLICK){
                    Log.i(TAG, "onTouchEvent: mTouchMode == TOUCH_MODE_CLICK");
                    if (Math.abs(distance) > mTouchSlop) {         //滚动事件
                        Log.i(TAG, "onTouchEvent: 大于--------------------");
                        mTouchMode = TOUCH_MODE_SCROLL;
                        Log.i(TAG, "onTouchEvent: 滚动事件");
                        //手指向左滑动，滚动的是mCurPageView
                        if (mDirection == MOVE_TO_LEFT){
                            mScrollView = mCurPageView;
                            //直接滚动distance距离即可，从右向左滑，distance为正值，向左滚动
                            //mScrollView.scrollTo(distance,0);
                        }else if (mDirection == MOVE_TO_RIGHT){     //手指向右滑动，滚动的是mPrePageView
                            mScrollView = mPrePageView;
                            //从左向右滑，distance为负值，向右滚动
                            //mScrollView.scrollTo(mWidthScreen+distance,0);
                        }

                    }
                }
                //滚动事件且有滚动方向
                //当没有上一页或者下一页时，就不会有滚动方向，页面也就不会滚动
                if (mTouchMode == TOUCH_MODE_SCROLL && mDirection != MOVE_NO_RESULT){
                    mScrollView.scrollTo(mDirection*mWidthScreen+distance,0);
                }
                break;
            case MotionEvent.ACTION_UP:              //这里处理手指松开时的滚动或者点击事件
                Log.i(TAG, "onTouchEvent: ---------------------------------------------------UP");
                //这里的处理时，只要有滑动距离了，就滑到下一页或上一页，不管其滑动的间距有没有超过屏幕宽度的1/3
                //不会重新回到当前页面
                //速度跟踪监测
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                if (Math.abs(xVelocity) > 50){
                    mTouchMode = TOUCH_MODE_FAST_SCROLLING;
                }
                Log.i(TAG, "onTouchEvent:   mDirection="+mDirection);
                Log.i(TAG, "onTouchEvent: ---------UP----------"+getScrollX());
                if (mTouchMode == TOUCH_MODE_SCROLL && mDirection != MOVE_NO_RESULT){           //普通滚动事件
                    Log.i(TAG, "onTouchEvent: UP 普通滚动事件");
                    int scrollX = mScrollView.getScrollX();
                    if (mDirection == MOVE_TO_LEFT){
                        if (scrollX > mWidthScreen/3){         //当前页面继续向左滚动
                            mTouchResult = MOVE_TO_LEFT;
                            smoothScrollBy(scrollX,mWidthScreen-scrollX);
                        }else{            //滚动回原来的位置
                            mTouchResult = MOVE_NO_RESULT;   //回滚状态取消触摸结果设置，防止滚动结束后，动态替换view
                            smoothScrollBy(scrollX,-scrollX);
                        }
                    }else if (mDirection == MOVE_TO_RIGHT){
                        if ((mWidthScreen-scrollX)>mWidthScreen/3){
                            mTouchResult = MOVE_TO_RIGHT;
                            smoothScrollBy(scrollX,-scrollX);
                        }else{
                            mTouchResult = MOVE_NO_RESULT;  //回滚状态取消触摸结果设置，防止滚动结束后，动态替换view
                            smoothScrollBy(scrollX,mWidthScreen-scrollX);
                        }
                    }
                    Log.i(TAG, "onTouchEvent: ---------UP----------"+getScrollX());
                }else if (mTouchMode == TOUCH_MODE_FAST_SCROLLING  && mDirection != MOVE_NO_RESULT){ //快速滚动模式
                    Log.i(TAG, "onTouchEvent: 快速滚动");
                    int scrollX = mScrollView.getScrollX();
                    if (mDirection == MOVE_TO_LEFT){
                        mTouchResult = MOVE_TO_LEFT;
                        smoothScrollBy(scrollX,mWidthScreen-scrollX);
                    }else if (mDirection == MOVE_TO_RIGHT){
                        mTouchResult = MOVE_TO_RIGHT;
                        smoothScrollBy(scrollX,-scrollX);
                    }
                }else if (mTouchMode == TOUCH_MODE_CLICK){                //点击事件
                    Log.i(TAG, "onTouchEvent: UP 点击事件");
                    if (mTouchEventListener == null)
                        return true;
                    if (mArea == LEFT_AREA){
                        //mTouchEventListener.onLeftClick();
                        if (mBookStateListener.hasPrePage()){
                            //判断上一页滚动的位置，如果还未来的及滚动到正确位置，则直接跳转到
                            mScrollView = mPrePageView;
                            mTouchResult = MOVE_TO_RIGHT;
                            smoothScrollBy(mScrollView.getScrollX(),-mWidthScreen);
                        }
                    }
                    else if (mArea == CENTER_AREA) {
                        Log.i(TAG, "onTouchEvent: "+mCurPageView);
                        mTouchEventListener.onCenterClick();
                        Log.i(TAG, "onTouchEvent: "+mCurPageView);
                        Log.i(TAG, "onTouchEvent: "+mCurPageView.getScrollX());
                    }
                    else if (mArea == RIGHT_AREA){
                        //mTouchEventListener.onRightClick();
                        //判断上一页滚动的位置，如果还未来的及滚动到正确位置，则直接跳转到
                        if (mBookStateListener.hasNextPage()){
                            mScrollView = mCurPageView;
                            mTouchResult = MOVE_TO_LEFT;
                            smoothScrollBy(mScrollView.getScrollX(),mWidthScreen);
                        }
                    }
                }
                mVelocityTracker.clear();
                //一次触摸事件结束后，重置变量
                resetVariables();
                break;
        }
        Log.i(TAG, "onTouchEvent: -----------------------END--------------------------------");
        return true;
    }

    private void resetVariables(){
        mDirection = MOVE_NO_RESULT;
        mTouchMode = TOUCH_MODE_CLICK;
    }

    private void smoothScrollBy(int startX, int dx){
        mScroller.startScroll(startX,0,dx,0,300);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()){
            Log.i(TAG, "computeScroll: mScroller.computeScrollOffset()");
            mScrollView.scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            Log.i(TAG, "computeScroll: "+mScrollView.getScrollX()+" "+mScrollView.getScrollY());
            postInvalidate();
        }else if(mScroller.isFinished() && mTouchEventListener != null && mBookStateListener != null
                && mTouchResult != MOVE_NO_RESULT){
            Log.i(TAG, "computeScroll: mScroller.isFinished()");
            if (mTouchResult == MOVE_TO_LEFT){       //向左滚动
                mTouchEventListener.scrollToNext();
                Log.i(TAG, "computeScroll: 向左滚动结束");
                removeView(mPrePageView);
                mPrePageView = mCurPageView;
                mCurPageView = mNextPageView;
                mNextPageView = mBookStateListener.createNextView();
                addView(mNextPageView,0);
            }else if (mTouchResult == MOVE_TO_RIGHT){       //向右滚动
                Log.i(TAG, "computeScroll: 向右滚动结束");
                mTouchEventListener.scrollToPre();
                removeView(mNextPageView);
                mNextPageView = mCurPageView;
                mCurPageView = mPrePageView;
                mPrePageView = mBookStateListener.createPreView();
                addView(mPrePageView);
                mPrePageView.scrollTo(mWidthScreen,0);
            }
            //重置为初始值
            mTouchResult = MOVE_NO_RESULT;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }

    public interface OnBookStateListener{
        boolean hasPrePage();
        boolean hasNextPage();
        View createPreView();
        View createNextView();
    }

    public interface OnTouchEventListener{
        void scrollToPre();
        void scrollToNext();
        void onCenterClick();
    }
}
