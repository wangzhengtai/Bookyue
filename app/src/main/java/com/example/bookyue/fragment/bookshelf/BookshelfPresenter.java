package com.example.bookyue.fragment.bookshelf;

import android.util.Log;

import com.example.bookyue.database.bean.Book;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class BookshelfPresenter implements IbookshelfPrenter{

    private static final String TAG = "BookshelfPresenter";

    private IbookshelfView mIbookshelfView;
    private IbookshelfModel mIbookshelfModel;

    BookshelfPresenter(IbookshelfView ibookshelfView){
        Log.i(TAG, "BookshelfPresenter: 构造方法");
        mIbookshelfView = ibookshelfView;
        mIbookshelfModel = new BookshelfModel(mIbookshelfView.getContext());
    }

    @Override
    public void initView() {
        Log.i(TAG, "initView: ");

       Observable.create(new ObservableOnSubscribe<List<Book>>(){
            @Override
            public void subscribe(ObservableEmitter<List<Book>> emitter) throws Exception {

                List<Book> books = mIbookshelfModel.getBooks();
                emitter.onNext(books);
                emitter.onComplete();
            }
       })
       .subscribeOn(Schedulers.io())
       .observeOn(AndroidSchedulers.mainThread())
       .subscribe(new Observer<List<Book>>() {
           @Override
           public void onSubscribe(Disposable d) {

           }

           @Override
           public void onNext(List<Book> books) {
               Log.i(TAG, "onNext: books"+books.toString());
               mIbookshelfView.initRecyclerView(books);
           }

           @Override
           public void onError(Throwable e) {
               Log.e(TAG, "onError: "+Thread.currentThread().getName(),e);
           }

           @Override
           public void onComplete() {
               Log.i(TAG, "onComplete: "+Thread.currentThread().getName());
           }
       });
    }

    @Override
    public void refreshView() {
        //这是在io线程执行的
        mIbookshelfView.showToast(mIbookshelfModel.refreshBooksFromNetwork());
        //这是在主线程        这个刷新似乎不能同步显示，因为线程不同步的原因(已解决)
        //用CountDownLatch之后，完美同步了
        Log.i(TAG, "refreshView: 我要开始刷新view了-----------------------------------");
        mIbookshelfView.refreshRecyclerView();
        Log.i(TAG, "refreshView: view刷新已经完成了-----------------------------------");
    }

    //此方法单纯的将数据源从model中暴露给fragment
    @Override
    public List<Book> getBooks() {
        Log.i(TAG, "getBooks: ");
        return mIbookshelfModel.getBooks();
    }

    @Override
    public void updateBookState(int position, int isUpdate) {
        mIbookshelfModel.updateBookState(position,isUpdate);
    }

    @Override
    public void detachView() {
        Log.i(TAG, "destroyView: ");
        mIbookshelfView = null;
    }

}
