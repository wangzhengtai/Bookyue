package com.example.bookyue.activity.read;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;

import com.example.bookyue.database.bean.Chapter;
import com.example.bookyue.database.dao.IBookDao;
import com.example.bookyue.database.dao.IChapterDao;
import com.example.bookyue.database.dao.impl.BookDaoImpl;
import com.example.bookyue.database.dao.impl.ChapterDaoImpl;
import com.example.bookyue.model.BookDetail;
import com.example.bookyue.model.ChapterBodyJson;
import com.example.bookyue.model.ChapterList;
import com.example.bookyue.network.ApiUtil;
import com.example.bookyue.network.ChapterUtil;
import com.example.bookyue.util.DensityUtil;
import com.example.bookyue.util.DrawTextUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ReadPresenter implements IReadPresenter {

    //数据源应该是放在presenter中的，presenter操作view和model的交互
    //数据和视图是不能完全分离的

    /* 对网络请求的总结：
    * 因为Android中网络请求必定不能主线程中进行，所以必须另开线程，所以用网络请求框架的时候，一般
    * 使用的都是异步请求方法，这一般来说都是正确的，但如果数据源与多个网络请求有关，即多个数据源全
    * 部加载完成后，才能初始化视图，这时候如果每个请求都是异步请求的话，就不太容易控制了，尤其是数
    * 据源加载是有顺序的情况的下，异步请求再控制其顺序，代码逻辑就会很混乱，不如自己开一个线程，然
    * 后再其中同步全部所以需要的网络请求，然后转回主线程，同步视图*/

    private IReadView mIReadView;
    private IChapterDao mIChapterDao;
    private IBookDao mIBookDao;

    private String mBookId;
    private String mBookTitle;
    private BookDetail mBookDetail;

    private int mIndexOfChapters;                //当前章节下标
    private int mIndexOfPages;                   //当前阅读页数

    private List<Chapter> mChapters;     //章节列表

    private String mPreChapterBody;             //前一章节内容
    private String mCurChapterBody;             //当前章节内容
    private String mNextChapterBody;            //下一章节内容

    private String[] mPreParagraphs;           //划分后的前一章节段落
    private String[] mCurParagraphs;           //划分后的当前章节段落
    private String[] mNextParagraphs;          //划分后的下一章节段落

    private List<Pair<Integer,Integer>> mPrePairs;             //保存前一章节页面绘制的排版
    private List<Pair<Integer,Integer>> mCurPairs;             //保存当前章节页面绘制的排版
    private List<Pair<Integer,Integer>> mNextPairs;            //保存下一章节页面绘制的排版

    private boolean first = false;               //用来判断是要初始化还是要刷新

    private static final String TAG = "ReadPresenter";

    ReadPresenter(IReadView IReadView) {
        mIReadView = IReadView;

        mBookId = ((Activity) mIReadView.getContext()).getIntent()
                .getStringExtra(ReadActivity.BOOK_ID);
        mBookTitle = ((Activity) mIReadView.getContext()).getIntent()
                .getStringExtra(ReadActivity.BOOK_TITLE);
//        mIndexOfChapters = ((Activity) mIReadView.getContext()).getIntent()
//                .getIntExtra(BookshelfFragment.BOOK_INDEX_OF_CHAPTERS,0);
//        mIndexOfPages = ((Activity) mIReadView.getContext()).getIntent()
//                .getIntExtra(BookshelfFragment.BOOK_INDEX_OF_PAGES,0);

        mIChapterDao = new ChapterDaoImpl(IReadView.getContext());
        mIBookDao = new BookDaoImpl(IReadView.getContext());
    }

    @Override
    public boolean haveThisBook() {
        return mIBookDao.haveThisBook(mBookId);
    }

    @Override
    public void initView() {
        Log.i(TAG, "initView: presenter");
        Observable.create(new ObservableOnSubscribe<List<Chapter>>() {       //io线程
            @Override
            public void subscribe(ObservableEmitter<List<Chapter>> emitter) throws Exception {
                mChapters = getSyncChaptersData();         //首先加载章节列表
                //这一切的前提都是数据库中有相应数据的时候
                if (mChapters.size() != 0) {     //不出意外，应该不是0，出了意外就是0了
                    getReadProgress();   //获取阅读进度，阅读进度必须获取最新的，所以初始化时从数据库中加载
                    mCurChapterBody = getSyncChapterBodyData(mIndexOfChapters);    //加载当前章节体
                    mCurParagraphs = mCurChapterBody.split("\n");
                    mCurPairs = calculatePages(mChapters.get(mIndexOfChapters).getTitle(),mCurParagraphs);
                    if (mIndexOfPages == 0 && mIndexOfChapters != 0){ //是某一章的第一页，且当前章节不是第一章
                        mPreChapterBody = getSyncChapterBodyData(mIndexOfChapters-1);    //加载上一章
                        mPreParagraphs = mPreChapterBody.split("\n");
                        mPrePairs = calculatePages(mChapters.get(mIndexOfChapters-1).getTitle(),mPreParagraphs);
                    }else if (mCurPairs != null && mIndexOfPages == mCurPairs.size()-1
                            && mIndexOfChapters != mChapters.size()-1){
                        //当前是某一章的最后一页，且当前章节不是最后一章
                        mNextChapterBody = getSyncChapterBodyData(mIndexOfChapters+1);     //加载下一章
                        mNextParagraphs = mNextChapterBody.split("\n");
                        mNextPairs = calculatePages(mChapters.get(mIndexOfChapters+1).getTitle(),mNextParagraphs);
                    }
                }
                Log.i(TAG, "subscribe: 数据请求完成了");
                //所有数据都准备好了，可以发送了
                emitter.onNext(mChapters);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Chapter>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Chapter> chapters) {
                Log.i(TAG, "onNext: ");
                mIReadView.initChapterList(chapters,mIndexOfChapters);      //初始化章节列表
                mIReadView.initReadView();                 //初始化阅读界面
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "initView onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "initView onComplete: Thread.currentThread().getName()="+
                        Thread.currentThread().getName());
            }
        });
    }

    //跳转章节时刷新阅读界面
    @Override
    public void refreshReadView() {
        //开线程执行数据加载工作
        Thread refreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mCurChapterBody = getSyncChapterBodyData(mIndexOfChapters);    //加载当前章节体
                mCurParagraphs = mCurChapterBody.split("\n");
                mCurPairs = calculatePages(mChapters.get(mIndexOfChapters).getTitle(),mCurParagraphs);
                //刷新时当前必定是某一章的第一页，需要上一章的数据     第一章例外
                if (mIndexOfChapters != 0) {
                    mPreChapterBody = getSyncChapterBodyData(mIndexOfChapters - 1);    //加载上一章
                    mPreParagraphs = mPreChapterBody.split("\n");
                    mPrePairs = calculatePages(mChapters.get(mIndexOfChapters - 1).getTitle(), mPreParagraphs);
                    Log.i(TAG, "run: 刷新数据加载完毕了。。。。。。。。。。。。。。。。。");
                }
            }
        });
        refreshThread.start();            //线程开始执行
        try {
            Log.i(TAG, "refreshReadView: 卡住了。。。。。。。。。。。。。。。。。。。。");
            refreshThread.join();         //数据未加载完，不允许执行下面的步骤
            Log.i(TAG, "refreshReadView: 释放了。。。。。。。。。。。。。。。。。。。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "refreshReadView: 开始刷新视图了。。。。。。。。。。。。。。。。。。。");
        mIReadView.refreshReadView();
    }

    @Override
    public String getChapterTitle(int index) {
        return mChapters.get(index).getTitle();
    }

    @Override
    public String[] getParagraphs(int index) {
        if (index == mIndexOfChapters){
            return mCurParagraphs;
        }else if (index == mIndexOfChapters-1){
            return mPreParagraphs;
        }else if (index == mIndexOfChapters+1){
            return mNextParagraphs;
        }
        return null;
    }

    @Override
    public Pair<Integer, Integer> getPair(int indexOfChapters,int index) {
        if (indexOfChapters == mIndexOfChapters){
            return mCurPairs.get(index);
        }else if (indexOfChapters == mIndexOfChapters-1){
            return mPrePairs.get(index);
        }else if (indexOfChapters == mIndexOfChapters+1){
            return mNextPairs.get(index);
        }
        return null;
    }

    @Override
    public int getChaptersSize() {
        return mChapters.size();
    }

    @Override
    public int getPages(int index) {
        if (index == mIndexOfChapters){
            return mCurPairs.size();
        }else if (index == mIndexOfChapters-1){
            return mPrePairs.size();
        }else if (index == mIndexOfChapters+1){
            return mNextPairs.size();
        }
        return 0;
    }

    @Override
    public int getIndexOfChapters() {
        return mIndexOfChapters;
    }

    @Override
    public int getIndexOfPages() {
        return mIndexOfPages;
    }

    @Override
    public void setIndexOfPages(int index) {
        mIndexOfPages = index;
    }

    //跳转章节时会调用这个方法
    @Override
    public void setIndexOfChapters(int indexOfChapters) {
        mIndexOfChapters = indexOfChapters;
    }

    @Override
    public void increaseIndexOfChapters() {
        mIndexOfChapters+=1;
        //
        mPreChapterBody = mCurChapterBody;
        mPreParagraphs = mCurParagraphs;
        mPrePairs = mCurPairs;

        mCurChapterBody = mNextChapterBody;
        mCurParagraphs = mNextParagraphs;
        mCurPairs = mNextPairs;

    }

    @Override
    public void decrementIndexOfChapters() {
        mIndexOfChapters-=1;
        //变化
        mNextChapterBody = mCurChapterBody;
        mNextParagraphs = mCurParagraphs;
        mNextPairs = mCurPairs;

        mCurChapterBody = mPreChapterBody;
        mCurParagraphs = mPreParagraphs;
        mCurPairs = mPrePairs;
    }

    @Override
    public void loadPreChapterData() {
        mPreChapterBody = getSyncChapterBodyData(mIndexOfChapters-1);    //加载上一章
        mPreParagraphs = mPreChapterBody.split("\n");
        mPrePairs = calculatePages(mChapters.get(mIndexOfChapters-1).getTitle(),mPreParagraphs);
    }

    @Override
    public void loadNextChapterData() {
        mNextChapterBody = getSyncChapterBodyData(mIndexOfChapters+1);
        mNextParagraphs = mNextChapterBody.split("\n");
        mNextPairs = calculatePages(mChapters.get(mIndexOfChapters+1).getTitle(),mNextParagraphs);
    }

    //获取阅读进度
    private void getReadProgress(){
        if (mIBookDao.haveThisBook(mBookId)){              //先判断
            Pair<Integer,Integer> pair = mIBookDao.getReadProgress(mBookId);
            if (pair != null){
                mIndexOfChapters = pair.first;
                mIndexOfPages = pair.second;
            }
            ApiUtil.getInstance().getBookDetail(mBookId, new Observer<BookDetail>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(BookDetail bookDetail) {
                    mBookDetail = bookDetail;
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
        }else {
            mIndexOfChapters = 0;
            mIndexOfPages = 0;
        }
    }

    @Override
    public void saveReadProgress() {
        mIBookDao.saveReadProgress(mBookId,mIndexOfChapters,mIndexOfPages);
    }

    @Override
    public void addBookToDatabase() {
        mIBookDao.addBook(mBookDetail);
    }

    @Override
    public void deleteBookDataFromDatabase() {
        //mIBookDao.deleteBook(mBookId);
        if (mIChapterDao.hasThisTable(mBookDetail.getTitle())){
            mIChapterDao.deleteChapterTable(mBookDetail.getTitle());
        }
    }

    private String getSyncChapterBodyData(int index) {
        //先从数据库中加载
        String body = getChapterBodyFromDatabase(index);
        if (body == null){
            //从网络中请求
            ChapterUtil chapterUtil = ChapterUtil.getInstance();
            body = chapterUtil.getSyncChapterBody(mChapters.get(index).getLink()).getChapter().getBody();
            //保存章节体到数据库中
            saveChapterBodyToDatabase(index,body);
            //内存中数据源更新
            mChapters.get(index).setCache(1);
        }
        Log.i(TAG, "getSyncChapterBodyData: 第"+index+"章的数据加载完成了");
        return body;
    }

    private List<Chapter> getSyncChaptersData(){
        //从数据库中加载
        List<Chapter> chapters = getChaptersFromDatabase();
        if (chapters == null || chapters.size() == 0){
            //从网络中请求
            ApiUtil apiUtil = ApiUtil.getInstance();
            chapters = new ArrayList<>(apiUtil.getSyncChapterList(mBookId).getMixToc().getChapters());
            //保存到数据库中
            saveChaptersToDatabase(chapters);
        }
        return chapters;
    }

    private List<Chapter> getChaptersFromDatabase() {
        Log.i(TAG, "getChaptersFromDatabase: Thread.currentThread().getName()="+
                Thread.currentThread().getName());
        if (!mIChapterDao.hasThisTable(mBookTitle)){       //若书籍对应的章节表不存在，则创建
            mIChapterDao.createBookChapterTable(mBookTitle);
            return null;        //直接返回，此时mChapters中size为0
        }
        //若对应的章节表存在，从章节表中读取
        return mIChapterDao.getChapters(mBookTitle);
    }

    //从数据库中尝试获取将要展示的章节内容
    //注意：此方法在Activity刚加载的时，即第一次调用时，会在getChaptersFromDatabase()方法后才调用
    //所以此时对应章节表必然存在 但章节体可能为null
    //注意调用的时机以及同步问题
//    private void getChapterBodyFromDatabase(int index){
//        if (index == mIndexOfChapters){          //当前章节
//            mCurChapterBody = mIChapterDao.getChapterBody(mBookTitle,mChapters.get(index).getTitle());
//            if (mCurChapterBody != null){
//                mCurParagraphs = mCurChapterBody.split("\n");
//                mCurPairs = calculatePages(mChapters.get(index).getTitle(),mCurParagraphs);
//            }
//        }else if (index == mIndexOfChapters -1){   //前一章节
//            mPreChapterBody = mIChapterDao.getChapterBody(mBookTitle,mChapters.get(index).getTitle());
//            if (mPreChapterBody != null){
//                mPreParagraphs = mPreChapterBody.split("\n");
//                mPrePairs = calculatePages(mChapters.get(index).getTitle(),mPreParagraphs);
//            }
//        }else if (index == mIndexOfChapters+1){             //下一章节
//            mNextChapterBody = mIChapterDao.getChapterBody(mBookTitle,mChapters.get(index).getTitle());
//            if (mNextChapterBody != null){
//                mNextParagraphs = mNextChapterBody.split("\n");
//                mNextPairs = calculatePages(mChapters.get(index).getTitle(),mNextParagraphs);
//            }
//        }
//    }

    private String getChapterBodyFromDatabase(int index){
        return mIChapterDao.getChapterBody(mBookTitle,mChapters.get(index).getTitle());
    }

    //此方法必定是在第一次打开书架中的书籍时才会调用，其他时机不会再调用
    //刷新时，也会调用，但现在刷新章节列表的方法还没写  睡觉！！！
    private void getChaptersFromNetwork(){
        ApiUtil apiUtil = ApiUtil.getInstance();
        //io线程请求网络并将其转化为List<Chapter>
        Log.i(TAG, "getChaptersFromNetwork: 章节列表的网络请求开始了");
        apiUtil.getChapterList(mBookId, new Function<ChapterList, List<Chapter>>() {
            @Override
            public List<Chapter> apply(ChapterList chapterList) throws Exception {
                Log.i(TAG, "getChaptersFromNetwork() apply: Thread.currentThread().getName()="
                        + Thread.currentThread().getName());
                mChapters.addAll(chapterList.getMixToc().getChapters());
                return mChapters;
            }
        }, new Consumer<List<Chapter>>() {        //io线程请求章节体
            @Override
            public void accept(List<Chapter> chapters) throws Exception {
                //在转化完后开始网络请求章节体
                //accept方法是在io线程调用的，同时getChapterBodyFromDatabase()方法内部也是在异步线程调用的
                //所以调用getChapterBodyFromDatabase()方法不会阻塞accept()方法，同时也不会阻塞调用链
                //此时只是提供一个开始网络请求章节体的时机，因为章节体的请求必然是要得到对应的link才行
                Log.i(TAG, "getChaptersFromNetwork() accept: 章节体网络请求开始了");
                //初始时，肯定是请求第一章，即mBookIndexIfChapters为0，同时mBookIndexIfChapters是默认为0的
                //此时的对应的linkEncode为null，所以需要编码得到
                //Retrofit会自动编码，所以不用自己编码！！！！！！！！！！！！！！！！！！！！！
                //此时只需要加载第一章即可
                getChapterBodyFromNetwork(mIndexOfChapters,chapters.get(mIndexOfChapters).getLink());
                Log.i(TAG, "getChaptersFromNetwork() accept: 执行章节体请求的accept()方法执行完成了");
            }
        }, new Consumer<List<Chapter>>() {         //主线程同步view
            @Override
            public void accept(List<Chapter> chapters) throws Exception {
                Log.i(TAG, "getChaptersFromNetwork() accept: Thread.currentThread().getName()="+
                        Thread.currentThread().getName());
                Log.i(TAG, "getChaptersFromNetwork() accept: 开始同步章节列表的视图了");
                if (!first){
                    mIReadView.initChapterList(chapters,mIndexOfChapters);           //此方法只会执行一次
                    //first = true;
                }
                else
                    mIReadView.refreshChapterList(chapters);
            }
        }, new Observer<List<Chapter>>() {          //io线程保存到数据库中
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Chapter> chapters) {
                Log.i(TAG, "getChaptersFromNetwork() onNext: Thread.currentThread().getName()="+
                        Thread.currentThread().getName());
                Log.i(TAG, "getChaptersFromNetwork() onNext: 章节列表要开始保存到数据库中了");
                saveChaptersToDatabase(chapters);
                Log.i(TAG, "getChaptersFromNetwork() onNext: 章节列表已经保存到数据库中了");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "getChaptersFromNetwork() onError: ",e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "getChaptersFromNetwork() onComplete: Thread.currentThread().getName()="+
                        Thread.currentThread().getName());
                Log.i(TAG, "getChaptersFromNetwork() onComplete: 章节列表的网络请求已经完成了");
            }
        });
    }

    //不需要编码！！！！！！！！！！！
    private void getChapterBodyFromNetwork(int index,String link){
        ChapterUtil chapterUtil = ChapterUtil.getInstance();
        Log.i(TAG, "getChapterBodyFromNetwork: 章节体的网络请求真正开始了");
        //io线程网络请求
        chapterUtil.getChapterBody(link, new Function<ChapterBodyJson, String>() {
            @Override
            public String apply(ChapterBodyJson chapterBodyJson) throws Exception {
                Log.i(TAG, "getChapterBodyFromNetwork apply: "+Thread.currentThread().getName());
                if (index == mIndexOfChapters){          //当前章节体的请求
                    mCurChapterBody = chapterBodyJson.getChapter().getBody();
                    if (mCurChapterBody != null){
                        mCurParagraphs = mCurChapterBody.split("\n");
                        mCurPairs = calculatePages(mChapters.get(index).getTitle(),mCurParagraphs);
                    }
                    return mCurChapterBody;
                }else if (index == mIndexOfChapters -1){   //前一章节
                    mPreChapterBody = chapterBodyJson.getChapter().getBody();
                    if (mPreChapterBody != null){
                        mPreParagraphs = mPreChapterBody.split("\n");
                        mPrePairs = calculatePages(mChapters.get(index).getTitle(),mPreParagraphs);
                    }
                    return mPreChapterBody;
                }else if (index == mIndexOfChapters+1){             //下一章节
                    mNextChapterBody = chapterBodyJson.getChapter().getBody();
                    if (mNextChapterBody != null){
                        mNextParagraphs = mNextChapterBody.split("\n");
                        mNextPairs = calculatePages(mChapters.get(index).getTitle(),mNextParagraphs);
                    }
                    return mNextChapterBody;
                }
                return null;
            }
        }, new Consumer<String>() {         //主线程更新或者显示
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, "getChapterBodyFromNetwork accept: 同步章节体视图了"+
                        Thread.currentThread().getName());
                if (!first){            //第一次初始化
                    Log.i(TAG, "getChapterBodyFromNetwork accept: 调用的是initReadView()方法");
                    if (mIndexOfPages == 0 && mIndexOfChapters != 0 && mCurChapterBody != null
                            && mPreChapterBody != null){
                        mIReadView.initReadView();
                    }else if (mCurPairs != null && mIndexOfPages == mCurPairs.size()-1
                            && mIndexOfChapters != mChapters.size()-1 && mCurChapterBody != null
                            && mNextChapterBody != null){
                        mIReadView.initReadView();
                    }else{         //此时只有mCurChapterBody不为null
                        mIReadView.initReadView();
                    }
                    //因为章节体的网络请求是晚于章节列表的网络请求的，所以在此处更新first
                    first = true;
                }else{
                    Log.i(TAG, "getChapterBodyFromNetwork accept: 调用的是refreshReadView()方法");
                    mIReadView.refreshReadView();
                }
            }
        }, new Observer<String>() {         //io线程章节体保存到数据库中
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, "getChapterBodyFromNetwork onNext: 章节体保存到数据库操作"+
                        Thread.currentThread().getName());
                saveChapterBodyToDatabase(index,s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: 这里出问题了？", e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "getChapterBodyFromNetwork onComplete: 章节体的网络流程走完了");
                Log.i(TAG, "getChapterBodyFromNetwork onComplete: "+Thread.currentThread().getName());
            }
        });
    }

    //保存之间肯定已查找过数据库了，所以相应章节表必定存在
    private void saveChaptersToDatabase(List<Chapter> chapters){
        //保存之前，mChapters已同步
//        for (Chapter chapter:mChapters){
//            try {
//                //URL编码
//                chapter.setLinkEncode(URLEncoder.encode(chapter.getLink(),"UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            mIChapterDao.addChapter(mBookTitle,chapter);
//        }
        mIChapterDao.addAllChapters(mBookTitle,chapters);
    }

    //保存的肯定是从网络请求得到的数据
    private void saveChapterBodyToDatabase(int index,String body){
        mIChapterDao.addChapterBody(mBookTitle,mChapters.get(index).getTitle(),body);
    }

    @Override
    public void detachView() {
        mIReadView = null;
    }

    //io线程计算
    //此方法只是单纯的计算，但未真正绘制页面
    //此方法计算一章内容的排版，并返回每页的起始段落下标以及段中的起始位置,List的容量即页面的数量
    private List<Pair<Integer,Integer>> calculatePages(String title,String[] paragraphs){
        Log.i(TAG, "calculatePages: "+title+" 的排版绘制工作开始了");
        Log.i(TAG, "calculatePages: 一共"+paragraphs.length+"段");
        List<Pair<Integer,Integer>> pairs = new ArrayList<>();

        Context context = mIReadView.getContext();

        DrawTextUtil drawTextUtil = DrawTextUtil.getInstance(context);

        Paint bigTitlePaint = drawTextUtil.getBigTitlePaint();
        Paint smallTitlePaint = drawTextUtil.getSmallTitlePaint();
        Paint paint = drawTextUtil.getTextPaint();
        int availableWidth = drawTextUtil.getAvailableWidth();                //屏幕可用宽度
        int X = drawTextUtil.getX();                      //初始时，X的坐标为左内边距的大小
        int Y = drawTextUtil.getY();                      //初始时，Y的坐标为上内边距的大小
        int lastY = drawTextUtil.getLastY();              //所允许绘制的最低高度
        float indentSize = drawTextUtil.getIndentSize();        //测量“你好”两字的宽度,并将其作为缩进的宽度
        int startTitle = 0;             //章节标题某一行绘制时的字符起始下标
        int endTitle = 0;               //章节标题某一行绘制时的字符终止下标
        int startParagraph = 0;         //章节体中某段某一行绘制时的字符起始下标
        int endParagraph = 0;           //章节体中某段某一行绘制时的字符终止下标
        float[] measuredWidth = {0};              //存储Paint.breakText()测量的宽度
        boolean first;                  //首行标识符
        float lineSpace = drawTextUtil.getLineSpace();

        for (int i=0;i<paragraphs.length;i++){    //段的循环
            if (i == 0){                      //第0段的肯定是在首页，需要绘制粗标题
                Pair<Integer,Integer> pair = new Pair<>(0,0);       //初始时第0段第0个字节
                Log.i(TAG, "calculatePages: 0 0");
                pairs.add(pair);

                Y += DensityUtil.dp2px(context,20);       //下移20dp
                while (endTitle<title.length()){
                    endTitle += bigTitlePaint.breakText(title,startTitle,title.length(),true,
                            availableWidth,measuredWidth);
                    Y += bigTitlePaint.getFontSpacing();
                    startTitle = endTitle;
                }
                Y += DensityUtil.dp2px(context,80);          //首页标题下空出100dp间距
                //重置 startTitle和endTitle
                startTitle = 0;
                endTitle = 0;
            }

            //新的一段
            first = true;                 //标识为首行
            X += indentSize;              //缩进两字符
            availableWidth -= indentSize;       //字符绘制的可用宽度也应相应减少

            while (endParagraph<paragraphs[i].length()){      //段中行的循环
                Log.i(TAG, "calculatePages: Y = "+Y);
                Y += paint.getFontSpacing()*lineSpace;
                Log.i(TAG, "calculatePages: Y += paint.getFontSpacing() "+Y);
                if (Y > lastY){    //将要绘制的下一行的字符的Y超过了laseY，则不允许绘制了，即到当前页的底部了
                    //一页已经绘制完成
                    Pair<Integer,Integer> pair = new Pair<>(i,endParagraph);
                    Log.i(TAG, "calculatePages: lastY = "+lastY);
                    Log.i(TAG, "calculatePages: "+i+" "+endParagraph);
                    pairs.add(pair);
                    Log.i(TAG, "calculatePages: 当前页数第"+pairs.size()+"页");

                    //重置Y的高度  变为初始值
                    Y = drawTextUtil.getY();

                    //可能正是一段的起始处
                    if (first){    //减去缩进
                        X -= indentSize;
                        availableWidth += indentSize;
                    }
                    //绘制一页开头的小标题
                    while (endTitle<title.length()){
                        Log.i(TAG, "calculatePages: 标题绘制时的高度 Y = "+Y);
                        endTitle += smallTitlePaint.breakText(title,startTitle,title.length(),true,
                                availableWidth,measuredWidth);
                        Y += smallTitlePaint.getFontSpacing();
                        startTitle = endTitle;
                    }
                    Log.i(TAG, "calculatePages: 标题绘制完的高度 Y = "+Y);
                    //Y += DensityUtil.dp2px(context,5);     //下移5dp
                    Log.i(TAG, "calculatePages: 标题下移20dp后的高度 Y = "+Y);
                    //重置 startTitle和endTitle
                    startTitle = 0;
                    endTitle = 0;

                    if (first){   //恢复缩进
                        X += indentSize;              //缩进两字符
                        availableWidth -= indentSize;
                    }

                    //标题绘制完后要下移一行！！！！！！！！！！！！！！！！！！！！！！！！！！！！
                    //为了给直接绘制的下一行留出空间
                    Y += paint.getFontSpacing()*lineSpace;
                }

                Log.i(TAG, "calculatePages: lastY - Y = "+(lastY - Y));
                endParagraph += paint.breakText(paragraphs[i],startParagraph,paragraphs[i].length(),
                        true, availableWidth,measuredWidth);
                Log.i(TAG, "calculatePages: "+paragraphs[i].substring(startParagraph,endParagraph));
                //只要是标点符号，就强加给上一行末尾
                while (endParagraph < paragraphs[i].length() &&
                        DrawTextUtil.isChinesePunctuation(paragraphs[i].charAt(endParagraph))){
                    endParagraph++;
                }
                startParagraph = endParagraph;
                if (first){           //当前首行绘制完成后，将缩进取消
                    X -= indentSize;
                    availableWidth += indentSize;
                    first = false;
                }
            }
            //一段绘制完成
            startParagraph = 0;           //一段绘制完成后，将段开始绘制下标重置为0
            endParagraph = 0;             //结束下标也重置为0
            Y += paint.getFontSpacing()*0.5;            //下移一行的间距
        }

        return pairs;
    }
}
