package com.example.bookyue.activity.BookDetail;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.bookyue.database.dao.IBookDao;
import com.example.bookyue.database.dao.IChapterDao;
import com.example.bookyue.database.dao.impl.BookDaoImpl;
import com.example.bookyue.database.dao.impl.ChapterDaoImpl;
import com.example.bookyue.model.BookDetail;
import com.example.bookyue.network.ApiUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BookDetailPresenter implements IBookDetailPresenter {

    private static final String TAG = "BookDetailPresenter";

    private IBookDetailView mIBookDetailView;
    private String mBookId;
    private BookDetail mBookDetail;               //保存书籍详情
    private Context mContext;

    private IBookDao mIBookDao;
    private IChapterDao mIChapterDao;

    BookDetailPresenter(IBookDetailView iBookDetailView){
        mIBookDetailView = iBookDetailView;
        mBookId = ((Activity) mIBookDetailView).getIntent().getStringExtra(BookDetailActivity.BOOK_ID);
        mContext = (Context) mIBookDetailView;
        mIBookDao = new BookDaoImpl(mContext);
        mIChapterDao = new ChapterDaoImpl(mContext);
    }


    @Override
    public void initView() {
        ApiUtil.getInstance().getBookDetail(mBookId, new Observer<BookDetail>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BookDetail bookDetail) {
                mBookDetail = bookDetail;
                mIBookDetailView.initView(bookDetail);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        });
    }

    @Override
    public boolean haveThisBook() {
        return mIBookDao.haveThisBook(mBookId);
    }

    @Override
    public String getBookId() {
        return mBookId;
    }

    @Override
    public String getBookTitle() {
        return mBookDetail.getTitle();
    }

    @Override
    public void addBookToDatabase() {
        mIBookDao.addBook(mBookDetail);
    }

    //此方法调用时，Book中一定有相关数据，但对应的章节表，不能确定，需判断
    @Override
    public void deleteBookDataFromDatabase() {
        mIBookDao.deleteBook(mBookId);
        if (mIChapterDao.hasThisTable(mBookDetail.getTitle())){
            mIChapterDao.deleteChapterTable(mBookDetail.getTitle());
        }
    }

    @Override
    public void detachView() {
        mIBookDetailView = null;
    }
}
