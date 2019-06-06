package com.example.bookyue.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.bookyue.model.BookDetail;
import com.example.bookyue.network.ApiUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.example.bookyue.database.DatabaseSchema.BookTable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final int VERSION = 1;

//    private static final String CREATE_BOOK = "create table "+ Book.NAME +" ("+
//            Book.Cols.ID+" primary key, "+
//            Book.Cols.TITLE+", "+
//            Book.Cols.COVER+", "+
//            Book.Cols.UPDATED+", "+
//            Book.Cols.LAST_CHAPTER+", "+
//            Book.Cols.IS_UPDATE+" )";

    private static final String CREATE_BOOK = "create table "+ BookTable.NAME +" ("+
            BookTable.Cols.ID+" text primary key, "+
            BookTable.Cols.TITLE+" text, "+
            BookTable.Cols.COVER+" text, "+
            BookTable.Cols.UPDATED+" text, "+
            BookTable.Cols.LAST_CHAPTER+" text, "+
            BookTable.Cols.INDEX_OF_CHAPTERS+" integer default 0, "+
            BookTable.Cols.INDEX_OF_PAGES+" integer default 0, "+
            BookTable.Cols.IS_SERIAL+" integer, "+
            BookTable.Cols.IS_UPDATE+" integer default 0)";

    public DatabaseHelper(@Nullable Context context) {
        super(context,DatabaseSchema.DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);

        //测试数据         这个只会执行一次      正好用来初始数据
        String[] ids ={"55eef8b27445ad27755670b9","59ba0dbb017336e411085a4e","5b1739ab4e66e33f75dca017",
                "592fe687c60e3c4926b040ca","5948c17d031fdaa005680400","5a589dbd46e30e144b871384",
                "53e56ee335f79bb626a496c9","542a5838a5ae10f815039a7f","591ed23b1861e2e332db308e",
                "555abb2d91d0eb814e5db04f","5ac06401fd4ae699163d587f","5afa99a6e1a9635af90fc802",
                "5a969060d9feab010066eaa3","5b0d28378659ea1aab8ca218","594ce7a37d50d2a82147c6a6",
                "59305ea9eeecff1975986572","559b51abadcc20911c4a3b16","5b2a2a9fb95aba51887cdac8",
                "5b063473318302b12a1cd4c4","5c1354ee7dac79116751a592","5b99d68f37feecdd4b8c7653",
                "57ceb2069acafda7326052ae","5b1e3d6af6149741037e6a63","5816b415b06d1d32157790b1"};

        Log.i(TAG, "subscribe: "+ Thread.currentThread().getName());
        ApiUtil apiUtil = ApiUtil.getInstance();
        for (String id:ids){
            //io线程
            apiUtil.getBookDetail(id, new Observer<BookDetail>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(BookDetail bookDetail) {          //主线程
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ContentValues values = new ContentValues();
                            values.put(BookTable.Cols.ID,bookDetail.get_id());
                            values.put(BookTable.Cols.TITLE,bookDetail.getTitle());
                            values.put(BookTable.Cols.COVER,bookDetail.getCover());
                            values.put(BookTable.Cols.UPDATED,bookDetail.getUpdated());
                            values.put(BookTable.Cols.LAST_CHAPTER,bookDetail.getLastChapter());
                            values.put(BookTable.Cols.IS_SERIAL,bookDetail.getIsSerial());
                            values.put(BookTable.Cols.IS_UPDATE,0);     //默认为0，未更新
                            db.insert(BookTable.NAME,null,values);
                            Log.i(TAG, "onNext: bookDetail"+bookDetail.get_id()+" "+bookDetail.getTitle()
                                    +" "+bookDetail.getAuthor());
                        }
                    }).start();
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "onError: " + Thread.currentThread().getName(), e);
                }

                @Override
                public void onComplete() {
                    Log.i(TAG, "onComplete: "+Thread.currentThread().getName());
                }
            });
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
