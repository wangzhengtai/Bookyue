package com.example.bookyue.activity.read;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookyue.ApplicationActivity;
import com.example.bookyue.R;
import com.example.bookyue.activity.BookDetail.BookDetailActivity;
import com.example.bookyue.adapter.ChapterAdapter;
import com.example.bookyue.adapter.OnClickListener;
import com.example.bookyue.database.bean.Chapter;
import com.example.bookyue.view.PageView;
import com.example.bookyue.view.ReadView;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ReadActivity extends ApplicationActivity implements View.OnClickListener,IReadView{

    private static final String TAG = "ReadActivity";

    //public static final String FROM = "from";            //判断其是由哪个activity跳转过来的
    public static final String BOOK_ID = "book_id";
    public static final String BOOK_TITLE = "book_title";

    private DrawerLayout mDrawerLayout;
    private PopupWindow mTopPopupWindow;
    private PopupWindow mBottomPopupWindow;
    private boolean isPopup;            //标识弹窗是否已弹出
    private String mBookId;
    private String mBookTitle;
    private RecyclerView mChaptersRecycler;
    private ChapterAdapter mChapterAdapter;
    private ReadView mReadView;

    private IReadPresenter mPresenter;

    private int mCurIndex;              //当前要展示的页面下标
    private int mPreIndex;              //上一页面下标
    private int mNextIndex;             //下一页面下标
    private int mCurIndexOfChapters;    //当前页面章节下标
    private int mPreIndexOfChapters;
    private int mNextIndexOfChapters;

    private View mPrePageView;
    private View mNextPageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_read);

        mBookId = getIntent().getStringExtra(BOOK_ID);
        mBookTitle = getIntent().getStringExtra(BOOK_TITLE);

        init();

        mPresenter = new ReadPresenter(this);
        mPresenter.initView();
    }

    void init(){
        mDrawerLayout = findViewById(R.id.read_drawer_layout);
        mChaptersRecycler = findViewById(R.id.chapter_directory);
        TextView sidebarTitle = findViewById(R.id.book_title_sidebar);
        sidebarTitle.setText(mBookTitle);
        //mChapterBodyRecycler = findViewById(R.id.read_recycler);
        mReadView = findViewById(R.id.read_view);

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                mChapterAdapter.setIndexOfChapters(mPresenter.getIndexOfChapters());
                mChapterAdapter.notifyDataSetChanged();        //打开窗口时，通知数据源已改变
                mChaptersRecycler.scrollToPosition(mPresenter.getIndexOfChapters());
            }
        });
    }

    private void initTopWindow(View v){
        View view = getLayoutInflater().inflate(R.layout.popup_window_read_top,null);
        mTopPopupWindow = new PopupWindow(view,LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,false);
        mTopPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
        //设置动画
        mTopPopupWindow.setAnimationStyle(R.style.top_window_anim_style);
        //设置popupWindow的位置
        mTopPopupWindow.showAtLocation(v, Gravity.TOP, 0, 0);
        TextView title = view.findViewById(R.id.title_top);
        ImageView bookInfo = view.findViewById(R.id.book_info_top);
        title.setText(mBookTitle);
        title.setOnClickListener(this);
        bookInfo.setOnClickListener(this);
    }

    private void initBottomMenu(View v){
        View view = getLayoutInflater().inflate(R.layout.popup_window_read_bottom,null);
        mBottomPopupWindow = new PopupWindow(view,LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,false);
        mBottomPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
        //设置动画
        mBottomPopupWindow.setAnimationStyle(R.style.bottom_window_anim_style);
        //设置popupWindow的位置
        mBottomPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);

        TextView previousChapter = view.findViewById(R.id.previous_chapter);
        SeekBar chapterSeekBar = view.findViewById(R.id.chapter_seek_bar);
        TextView nextChapter = view.findViewById(R.id.next_chapter);
        TextView directory = view.findViewById(R.id.directory_bottom);
        TextView night = view.findViewById(R.id.night_bottom);
        TextView cache = view.findViewById(R.id.cache_bottom);
        TextView settings = view.findViewById(R.id.settings_bottom);

        previousChapter.setOnClickListener(this);
        nextChapter.setOnClickListener(this);
        directory.setOnClickListener(this);
        night.setOnClickListener(this);
        cache.setOnClickListener(this);
        settings.setOnClickListener(this);

        chapterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void popupOrCloseWindows(View v){
        if (!isPopup){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (mTopPopupWindow == null && mBottomPopupWindow == null){
                initTopWindow(v);
                initBottomMenu(v);
            }else{
                mTopPopupWindow.showAtLocation(v, Gravity.TOP, 0, 0);
                mBottomPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
            }
        }else{
            mTopPopupWindow.dismiss();
            mBottomPopupWindow.dismiss();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        isPopup = !isPopup;
    }

    /**
     * 设置屏幕的背景透明度
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void initChapterList(List<Chapter> chapters,int indexOfChapters) {
        mChapterAdapter = new ChapterAdapter(chapters,this);
        mChapterAdapter.setIndexOfChapters(indexOfChapters);
        mChapterAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(int position) {
                jumpToChapter(position);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mChaptersRecycler.setLayoutManager(layoutManager);
        mChaptersRecycler.setAdapter(mChapterAdapter);
    }

    @Override
    @SuppressLint("InflateParams")
    public void initReadView() {
        mCurIndex = mPresenter.getIndexOfPages();              //当前要展示的页面下标
        mPreIndex = mCurIndex - 1;        //上一页面下标
        mNextIndex = mCurIndex + 1;       //下一页面下标
        mCurIndexOfChapters = mPresenter.getIndexOfChapters();    //当前页面章节下标
        mPreIndexOfChapters = mCurIndexOfChapters;
        mNextIndexOfChapters = mCurIndexOfChapters;
        //------------------------------------------------------------------------------------------
        View prePageView = createPreView();
        //------------------------------------------------------------------------------------------
        View curPageView = createCurView();
        //------------------------------------------------------------------------------------------
        View nextPageView = createNextView();
        //------------------------------------------------------------------------------------------
        //初始化    哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈
        mReadView.initView(prePageView,curPageView,nextPageView);
        //书籍状态监听器
        mReadView.setOnBookStateListener(new ReadView.OnBookStateListener() {
            @Override
            public boolean hasPrePage() {
                return !(mCurIndexOfChapters == 0 && mCurIndex == 0);           //只要不是第一章第一页
            }

            @Override
            public boolean hasNextPage() {
                return !(mCurIndexOfChapters == mPresenter.getChaptersSize()-1 &&
                        mCurIndex == mPresenter.getPages(mCurIndexOfChapters)-1);    //只要不是最后一章最后一页
            }

            @Override
            public View createPreView() {
                Log.i(TAG, "createPreView: createPreView()开始了");
                mPreIndexOfChapters = mCurIndexOfChapters;
                mPreIndex = mCurIndex-1;
                if (mCurIndex == 0 && mCurIndexOfChapters != 0){     //当前是第一页
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //拷贝代码容易出错！！！！！！！！！！！！！！！！！！！！！！！！！！！
                            mPresenter.loadPreChapterData();          //需要加载上一章的内容
                            mPrePageView = ReadActivity.this.createPreView();
                            countDownLatch.countDown();
                        }
                    }).start();
                    try {
                        countDownLatch.await();         //阻塞
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    mPrePageView = ReadActivity.this.createPreView();
                }

//                Observable.create(new ObservableOnSubscribe<String>(){
//                    @Override
//                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
//                        String body = null;
//                        if (mCurIndex == 0 && mCurIndexOfChapters != 0){     //当前是第一页
//                            body = mPresenter.loadPreChapterData();          //需要加载上一章的内容
//                        }
//                        emitter.onNext(body);
//                        emitter.onComplete();
//                    }
//                }).subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .doOnNext(new Consumer<String>() {
//                            @Override
//                            public void accept(String s) throws Exception {
//                                mPrePageView = ReadActivity.this.createPreView();
//                                Log.i(TAG, "accept: 上一页创建完成了");
//                                countDownLatch.countDown();
//                            }
//                        }).observeOn(Schedulers.io())
//                        .subscribe(new Observer<String>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//
//                            }
//
//                            @Override
//                            public void onNext(String s) {
//                                if (s != null)
//                                    mPresenter.saveChapterBodyToDatabase(mPreIndexOfChapters,s);
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                Log.e(TAG, "onError: ",e);
//                            }
//
//                            @Override
//                            public void onComplete() {
//                                Log.i(TAG, "onComplete: ");
//                            }
//                        });
//                try {
//                    countDownLatch.await();          //阻塞
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                Log.i(TAG, "createPreView: createPreView()返回了");
                return mPrePageView;
            }

            @Override
            public View createNextView() {
                Log.i(TAG, "createNextView: createNextView()开始了");
                //注意此时的下标已经变化过来了
                mNextIndexOfChapters = mCurIndexOfChapters;
                mNextIndex = mCurIndex+1;

                if (mCurIndex == mPresenter.getPages(mCurIndexOfChapters)-1
                        && mCurIndexOfChapters != mPresenter.getChaptersSize()-1){    //当前是最后一页
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mPresenter.loadNextChapterData();          //需要加载下一章的内容
                            mNextPageView = ReadActivity.this.createNextView();
                            Log.i(TAG, "run: createNextView完毕了");
                            countDownLatch.countDown();
                            Log.i(TAG, "run: 下一章节已经加载完毕了");
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Log.i(TAG, "run: runOnUiThread()已经开始了");
//                                    mNextPageView = ReadActivity.this.createNextView();
//                                    Log.i(TAG, "run: createNextView完毕了");
//                                    countDownLatch.countDown();
//                                }
//                            });
                        }
                    }).start();
                    try {
                        Log.i(TAG, "createNextView: 阻塞中。。。。。。。。。。。。。。。");
                        countDownLatch.await();         //阻塞
                        Log.i(TAG, "createNextView: 阻塞结束。。。。。。。。。。。。。。。");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    mNextPageView = ReadActivity.this.createNextView();
                }

//                Observable.create(new ObservableOnSubscribe<String>(){
//                    @Override
//                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
//                        String body = "null";
//                        if (mCurIndex == mPresenter.getPages(mCurIndexOfChapters)-1
//                                && mCurIndexOfChapters != mPresenter.getChaptersSize()){    //当前是最后一页
//                            body = mPresenter.loadNextChapterData();          //需要加载下一章的内容
//                        }
//                        Log.i(TAG, "subscribe: next 开始发送了");
//                        emitter.onNext(body);          //onNext()不能发送null指针
//                        Log.i(TAG, "subscribe: body "+body.length());
//                        if (emitter.isDisposed()){
//                            Log.i(TAG, "subscribe: true");
//                        }else{
//                            Log.i(TAG, "subscribe: false");
//                        }
//                        emitter.onComplete();
//                    }
//                }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        Log.i(TAG, "accept: 开始创建下一页了");
//                        mNextPageView = ReadActivity.this.createNextView();
//                        Log.i(TAG, "accept: 下一页创建完成");
//                        countDownLatch.countDown();
//                    }
//                }).observeOn(Schedulers.io())
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//                        Log.i(TAG, "onNext: 直接调用的是这个方法吗？");
//                        if (!s.equals("null"))
//                            mPresenter.saveChapterBodyToDatabase(mNextIndexOfChapters,s);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "onError: ",e);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.i(TAG, "onComplete: ");
//                    }
//                });
//                try {
//                    countDownLatch.await();          //阻塞
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                Log.i(TAG, "createNextView: createNextView()返回了");
                return mNextPageView;
            }
        });
        //触摸事件监听器
        mReadView.setOnTouchEventListener(new ReadView.OnTouchEventListener() {
            @Override
            public void scrollToPre() {              //前一页变当前页
                if (mPreIndexOfChapters != mCurIndexOfChapters){
                    mPresenter.decrementIndexOfChapters();           //章节下标减一
                }
                mPresenter.setIndexOfPages(mPreIndex);               //更新页面数
                //下标变换
                mNextIndexOfChapters = mCurIndexOfChapters;
                mNextIndex = mCurIndex;
                mCurIndexOfChapters = mPreIndexOfChapters;
                mCurIndex = mPreIndex;
            }

            @Override
            public void scrollToNext() {                       //原先的下一页面变成当前页面
                if (mNextIndexOfChapters != mCurIndexOfChapters){
                    mPresenter.increaseIndexOfChapters();
                }
                mPresenter.setIndexOfPages(mNextIndex);
                //下标变换
                mPreIndexOfChapters = mCurIndexOfChapters;
                mPreIndex = mCurIndex;
                mCurIndexOfChapters = mNextIndexOfChapters;
                mCurIndex = mNextIndex;
            }

            @Override
            public void onCenterClick() {
                popupOrCloseWindows(mDrawerLayout);
            }
        });
    }

    @SuppressLint("InflateParams")
    private View createPreView(){
        View prePageView;
        if (mCurIndexOfChapters == 0 && mCurIndex == 0) {     //当前是第一章第一页
            prePageView = new View(this);       //前一页直接设置成view，反正翻不过去
        }else{
            prePageView = LayoutInflater.from(getContext()).inflate(R.layout.text_page,
                    null,false);
            PageView pageTop = prePageView.findViewById(R.id.text_page);
            if (mPreIndex < 0){           //说明上一页应该显示的是上一章的最后一页
                mPreIndexOfChapters = mCurIndexOfChapters-1;
                mPreIndex = mPresenter.getPages(mPreIndexOfChapters)-1;        //上一章最后一页
            }
            pageTop.setTitle(mPresenter.getChapterTitle(mPreIndexOfChapters));
            pageTop.setParagraphs(mPresenter.getParagraphs(mPreIndexOfChapters));
            pageTop.setPages(mPresenter.getPages(mPreIndexOfChapters));
            pageTop.setIndexOfPages(mPreIndex);
            pageTop.setIndexOfParagraphs(mPresenter.getPair(mPreIndexOfChapters,mPreIndex).first);
            pageTop.setStartParagraph(mPresenter.getPair(mPreIndexOfChapters,mPreIndex).second);
        }
        Log.i(TAG, "createPreView: 创建了前一个视图对象");
        return prePageView;
    }

    @SuppressLint("InflateParams")
    private View createCurView(){
        View curPageView = LayoutInflater.from(getContext()).inflate(R.layout.text_page,
                null,false);
        PageView pageCenter = curPageView.findViewById(R.id.text_page);
        pageCenter.setTitle(mPresenter.getChapterTitle(mCurIndexOfChapters));
        pageCenter.setParagraphs(mPresenter.getParagraphs(mCurIndexOfChapters));
        pageCenter.setPages(mPresenter.getPages(mCurIndexOfChapters));
        pageCenter.setIndexOfPages(mCurIndex);
        pageCenter.setIndexOfParagraphs(mPresenter.getPair(mCurIndexOfChapters,mCurIndex).first);
        pageCenter.setStartParagraph(mPresenter.getPair(mCurIndexOfChapters,mCurIndex).second);
        return curPageView;
    }

    @SuppressLint("InflateParams")
    private View createNextView(){
        View nextPageView;
        if (mCurIndex == mPresenter.getPages(mCurIndexOfChapters)-1
                && mCurIndexOfChapters == mPresenter.getChaptersSize()-1){          //当前页面是最后一章最后一页
            nextPageView = new View(this);
        }else{
            nextPageView = LayoutInflater.from(getContext()).inflate(R.layout.text_page,
                    null,false);
            PageView pageBottom = nextPageView.findViewById(R.id.text_page);
            if (mNextIndex == mPresenter.getPages(mCurIndexOfChapters)){       //页面数超标
                mNextIndexOfChapters = mCurIndexOfChapters+1;           //章节下标加1
                mNextIndex = 0;                                         //首页
            }
            pageBottom.setTitle(mPresenter.getChapterTitle(mNextIndexOfChapters));
            pageBottom.setParagraphs(mPresenter.getParagraphs(mNextIndexOfChapters));
            pageBottom.setPages(mPresenter.getPages(mNextIndexOfChapters));
            pageBottom.setIndexOfPages(mNextIndex);
            pageBottom.setIndexOfParagraphs(mPresenter.getPair(mNextIndexOfChapters,mNextIndex).first);
            pageBottom.setStartParagraph(mPresenter.getPair(mNextIndexOfChapters,mNextIndex).second);
        }
        Log.i(TAG, "createNextView: 创建了后一个视图对象");
        return nextPageView;
    }

    @Override
    public void refreshChapterList(List<Chapter> chapters) {
        mChapterAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshReadView() {
        //更新下标
        mCurIndex = mPresenter.getIndexOfPages();              //当前要展示的页面下标
        mPreIndex = mCurIndex - 1;        //上一页面下标
        mNextIndex = mCurIndex + 1;       //下一页面下标
        mCurIndexOfChapters = mPresenter.getIndexOfChapters();    //当前页面章节下标
        mPreIndexOfChapters = mCurIndexOfChapters;
        mNextIndexOfChapters = mCurIndexOfChapters;
        //重新创建视图对象
        //------------------------------------------------------------------------------------------
        View prePageView = createPreView();
        //------------------------------------------------------------------------------------------
        View curPageView = createCurView();
        //------------------------------------------------------------------------------------------
        View nextPageView = createNextView();
        //------------------------------------------------------------------------------------------
        //刷新   替换原有视图
        mReadView.refreshView(prePageView,curPageView,nextPageView);
    }

    //跳转到指定章节
    private void jumpToChapter(int indexOfChapters){
        mPresenter.setIndexOfChapters(indexOfChapters);      //重新设置章节下标
        mPresenter.setIndexOfPages(0);                       //将当前页面设置为第一页
        mPresenter.refreshReadView();                        //加载数据，刷新界面
        mDrawerLayout.closeDrawer(GravityCompat.START,false);      //关闭章节列表
    }

    private void showToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_top:
                finish();
                break;
            case R.id.book_info_top:
                Intent intent = new Intent(this, BookDetailActivity.class);
                intent.putExtra(BookDetailActivity.BOOK_ID,mBookId);
                startActivity(intent);
                break;
            case R.id.previous_chapter:
                break;
            case R.id.next_chapter:
                break;
            case R.id.directory_bottom:
                mDrawerLayout.openDrawer(GravityCompat.START);
                //此时传入的v，完全没有用到，因为此时执行的必然是关闭窗口操作
                popupOrCloseWindows(v);
                break;
            case R.id.night_bottom:
                break;
            case R.id.cache_bottom:
                break;
            case R.id.settings_bottom:
                break;
            default:
        }
    }

//    @Override
//    public void onClick(int position) {
//        popupOrCloseWindows(mDrawerLayout);
//    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (!mPresenter.haveThisBook()){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("添书");
            dialog.setMessage("将本书添加到书架？");
            dialog.setCancelable(false);
            dialog.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPresenter.addBookToDatabase();
                    finish();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPresenter.deleteBookDataFromDatabase();
                    finish();
                }
            });
            dialog.show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        mPresenter.saveReadProgress();               //保存阅读进度
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //若窗口未关闭，关闭窗口
        if (isPopup){
            mTopPopupWindow.dismiss();
            mTopPopupWindow = null;
            mBottomPopupWindow.dismiss();
            mBottomPopupWindow = null;
        }
        mPresenter.detachView();
        super.onDestroy();
    }
}
