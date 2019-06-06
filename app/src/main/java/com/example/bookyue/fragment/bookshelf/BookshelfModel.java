package com.example.bookyue.fragment.bookshelf;

import android.content.Context;
import android.util.Log;

import com.example.bookyue.R;
import com.example.bookyue.database.bean.Book;
import com.example.bookyue.database.dao.IBookDao;
import com.example.bookyue.database.dao.impl.BookDaoImpl;
import com.example.bookyue.model.BookDetail;
import com.example.bookyue.network.ApiUtil;
import com.example.bookyue.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class BookshelfModel implements IbookshelfModel {

    private static final String TAG = "BookshelfModel";

    private IBookDao mIBookDao;
    private List<Book> mBooks;
    private Context mContext;

    BookshelfModel(Context context){
        Log.i(TAG, "BookshelfModel: 构造方法");
        mContext = context;
        mIBookDao = new BookDaoImpl(context);
        mBooks = new ArrayList<>();
    }

    @Override
    public int refreshBooksFromNetwork() {
        //书架中没有书籍
        if (mBooks.size() == 0)
            return R.string.add_books;
        //当前无网络
        if (!NetworkUtil.isConnected(mContext))
            return R.string.network_cannot_connect;
        //走网络获取书籍的最新状态
        ApiUtil apiUtil = ApiUtil.getInstance();
        final CountDownLatch countDownLatch = new CountDownLatch(mBooks.size());
        for (Book book:mBooks){
            Observable.create(new ObservableOnSubscribe<BookDetail>() {
                @Override
                public void subscribe(ObservableEmitter<BookDetail> emitter) throws Exception {
                    BookDetail bookDetail = apiUtil.getSyncBookDetail(book.get_id());
                    emitter.onNext(bookDetail);
                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .subscribe(new Observer<BookDetail>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BookDetail bookDetail) {
                            //短时间连续两次请求的话，bookDetail虽然不为空，但bookDetail中属性会为空
                            if (bookDetail.get_id() == null){
                                return;         //直接返回
                            }
                            updateBookInfo(book,bookDetail);
                        }

                        @Override
                        public void onError(Throwable e) {
                            countDownLatch.countDown();
                        }

                        @Override
                        public void onComplete() {
                            countDownLatch.countDown();
                        }
                    });

//            new Thread(new Runnable() {            //开线程
//                @Override
//                public void run() {
//                    updateBookInfo(book,apiUtil.getSyncBookDetail(book.get_id()));
//                    countDownLatch.countDown();
//                }
//            }).start();
        }



//        for (final Book book:mBooks){
//            //这是在io线程操作的，非主线程
//            apiUtil.getBookDetail(book.get_id(), new Observer<BookDetail>() {
//                @Override
//                public void onSubscribe(Disposable d) {
//
//                }
//
//                @Override
//                public void onNext(BookDetail bookDetail) {        //这是主线程，卧槽！！！
//                    Log.i(TAG, "onNext: bookDetail "+bookDetail+" "+ bookDetail.get_id()+" "
//                            +bookDetail.getTitle() +" "+bookDetail.getAuthor());
//                    //短时间连续两次请求的话，bookDetail虽然不为空，但bookDetail中属性会为空
//                    if (bookDetail.get_id() == null){
//                        return;         //直接返回
//                    }
//                    //Log.i(TAG, "onNext: book "+System.identityHashCode(book));
//                    updateBookInfo(book,bookDetail);
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    Log.e(TAG, "onError: " + Thread.currentThread().getName(), e);
//                    countDownLatch.countDown();          //避免出错时阻塞
//                }
//
//                @Override
//                public void onComplete() {
//                    Log.i(TAG, "onComplete: "+Thread.currentThread().getName());
//                    countDownLatch.countDown();
//                    Log.i(TAG, "onComplete: 数据刷新完成了---------------------------------");
//                    Log.i(TAG, "onComplete: countDownLatch的数量 "+countDownLatch.getCount());
//                }
//            });
//        }

        try {
            countDownLatch.await();           //等待一直到所有的网络请求结束，才允许才方法返回
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return R.string.refresh_success;
    }

    //book表信息
    //_id   title   cover(书籍封面)   updated(最新更新时间)   lastChapter(最新章节)
    // isSerial(是否连载中)    isUpdate(是否更新 int类型)
    //_id title cover一般不会改变
    //传入数据库对象，防止重复创建生成大量实例
    private void updateBookInfo(Book book, BookDetail bookDetail){
        //打印内存地址 观察sqLiteDatabase是否是同一实例
        //Log.i(TAG, "updateBookInfo: sqLiteDatabase "+System.identityHashCode(sqLiteDatabase));
        //Log.i(TAG, "updateBookInfo: book "+System.identityHashCode(book));
        //测试是否能同步  结果：能
        //book.setLastChapter("123456789");

        //如果book的最新更新时间与请求网络得到的bookDetail的最新更新时间是一样的，则认为书籍状态未改变
        //即未更新，则无需同步操作
        Log.i(TAG, "updateBookInfo: 书籍是否更新---------------------------------------------");
        Log.i(TAG, "updateBookInfo: "+book.getUpdated());
        Log.i(TAG, "updateBookInfo: "+bookDetail.getUpdated());
        if (book.getUpdated().equals(bookDetail.getUpdated()))
            return;
        //同步mBooks 即内存中数据源
        book.setUpdated(bookDetail.getUpdated());
        book.setLastChapter(book.getLastChapter());
        book.setIsUpdate(1);
        //同步数据库
        mIBookDao.updateBook(bookDetail);
    }

    private void getBooksFromDataBase() {
        Log.i(TAG, "getBooksFromDataBase: "+Thread.currentThread().getName());
        mIBookDao.getBooks(mBooks);
        Log.i(TAG, "getBooksFromDataBase: "+mBooks.size());
    }

    @Override
    public List<Book> getBooks() {
        Log.i(TAG, "getBooks: ");
        if (mBooks.size() == 0){       //从数据库中加载
            getBooksFromDataBase();
        }
        return mBooks;
    }

    @Override
    public void updateBookState(int position, int isUpdate) {
        mIBookDao.updateBookState(mBooks.get(position).get_id(),isUpdate);
    }
}
