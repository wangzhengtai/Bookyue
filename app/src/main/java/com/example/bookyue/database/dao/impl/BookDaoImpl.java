package com.example.bookyue.database.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.example.bookyue.database.DatabaseHelper;
import com.example.bookyue.database.bean.Book;
import com.example.bookyue.database.dao.IBookDao;
import com.example.bookyue.model.BookDetail;

import java.util.List;

import static com.example.bookyue.database.DatabaseSchema.*;

public class BookDaoImpl implements IBookDao {

    private SQLiteDatabase mSQLiteDatabase;

    private static final String TAG = "BookDaoImpl";

    public BookDaoImpl(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        mSQLiteDatabase = databaseHelper.getWritableDatabase();
    }

    @Override
    public boolean haveThisBook(String bookId) {
        Cursor cursor = mSQLiteDatabase.rawQuery("select count(_id) from Book where _id = ?",
                new String[]{bookId});
        boolean flag = false;
        Log.i(TAG, "haveThisBook: "+cursor.getCount());
        if (cursor.moveToFirst()){
            flag = true;
        }
        cursor.close();
        return flag;
    }

    @Override
    public void addBook(BookDetail bookDetail) {
        ContentValues values = new ContentValues();
        values.put(BookTable.Cols.ID,bookDetail.get_id());
        values.put(BookTable.Cols.TITLE,bookDetail.getTitle());
        values.put(BookTable.Cols.COVER,bookDetail.getCover());
        values.put(BookTable.Cols.UPDATED,bookDetail.getUpdated());
        values.put(BookTable.Cols.LAST_CHAPTER,bookDetail.getLastChapter());
        values.put(BookTable.Cols.IS_SERIAL,bookDetail.getIsSerial());
        //values.put(BookTable.Cols.IS_UPDATE,0);     //默认为0，未更新
        mSQLiteDatabase.insert(BookTable.NAME,null,values);
    }

    @Override
    public void getBooks(List<Book> books) {
        Cursor cursor = mSQLiteDatabase.query(BookTable.NAME,
                null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do{
                Book book = new Book();
                book.set_id(cursor.getString(cursor.getColumnIndex(BookTable.Cols.ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(BookTable.Cols.TITLE)));
                book.setCover(cursor.getString(cursor.getColumnIndex(BookTable.Cols.COVER)));
                book.setUpdated(cursor.getString(cursor.getColumnIndex(BookTable.Cols.UPDATED)));
                book.setLastChapter(cursor.getString(cursor.getColumnIndex(BookTable.Cols.LAST_CHAPTER)));
                book.setIndexOfChapters(cursor.getInt(cursor.getColumnIndex(BookTable.Cols.INDEX_OF_CHAPTERS)));
                book.setIndexOfPages(cursor.getInt(cursor.getColumnIndex(BookTable.Cols.INDEX_OF_PAGES)));
                book.setIsSerial(cursor.getInt(cursor.getColumnIndex(BookTable.Cols.IS_SERIAL)));
                book.setIsUpdate(cursor.getInt(cursor.getColumnIndex(BookTable.Cols.IS_UPDATE)));
                books.add(book);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void updateBook(BookDetail bookDetail) {
        Log.i(TAG, "updateBook: 数据库同步了吗？？？？？？？？？？？？？？？");
        ContentValues values = new ContentValues();
        values.put(BookTable.Cols.UPDATED,bookDetail.getUpdated());
        values.put(BookTable.Cols.LAST_CHAPTER,bookDetail.getLastChapter());
        values.put(BookTable.Cols.IS_UPDATE,1);        //1表示书籍已更新
        int result = mSQLiteDatabase.update(BookTable.NAME,values,"_id = ?",
                new String[]{bookDetail.get_id()});
        Log.i(TAG, "updateBook: _id = "+bookDetail.get_id());
        Log.i(TAG, "updateBook: 结果："+result);
    }

    //更新书籍状态  已更新 未更新   此方法用于点击书架中的书籍时取消更新状态
    @Override
    public void updateBookState(String _id, int isUpdate) {
        mSQLiteDatabase.execSQL("update "+BookTable.NAME+" set "+BookTable.Cols.IS_UPDATE+" = ? where "+
                BookTable.Cols.ID+" = ?",new Object[]{isUpdate,_id});
    }

    @Override
    public Pair<Integer, Integer> getReadProgress(String bookId) {
        Cursor cursor = mSQLiteDatabase.rawQuery("select indexOfChapters, indexOfPages from Book where _id = ?",
                new String[]{bookId});
        Pair<Integer,Integer> pair = null;
        if (cursor.moveToFirst()){
            pair = new Pair<>(cursor.getInt(cursor.getColumnIndex(BookTable.Cols.INDEX_OF_CHAPTERS)),
                    cursor.getInt(cursor.getColumnIndex(BookTable.Cols.INDEX_OF_PAGES)));
        }
        cursor.close();
        return pair;
    }

    @Override
    public void saveReadProgress(String bookId,int indexOfChapters, int indexOfPages) {
        mSQLiteDatabase.execSQL("update Book set indexOfChapters = ?, indexOfPages = ? where _id = ?",
                new Object[]{indexOfChapters,indexOfPages,bookId});
    }

    @Override
    public void deleteBook(String bookId) {
        mSQLiteDatabase.execSQL("delete from Book where _id = ?",new Object[]{bookId});
    }

}
