package com.example.bookyue.database.dao.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.bookyue.database.DatabaseHelper;
import com.example.bookyue.database.bean.Chapter;
import com.example.bookyue.database.dao.IChapterDao;

import java.util.ArrayList;
import java.util.List;

import static com.example.bookyue.database.DatabaseSchema.*;

public class ChapterDaoImpl implements IChapterDao {

    private static final String TAG = "ChapterDaoImpl";

    private SQLiteDatabase mSQLiteDatabase;
    private Context mContext;

    public ChapterDaoImpl(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        mSQLiteDatabase = databaseHelper.getWritableDatabase();
        mContext = context;
    }

    @Override
    public boolean hasThisTable(String tableName) {
        boolean exists = false;
        Cursor cursor = mSQLiteDatabase.rawQuery("select count(*) from Sqlite_master  " +
                        "where type ='table' and name ='"+tableName.trim()+"'",null);
        if (cursor.moveToFirst()){
            int count = cursor.getInt(0);
            if(count>0){
                exists = true;
            }
        }
        cursor.close();
        Log.i(TAG, "hasThisTable: "+tableName+" "+exists);
        return exists;
    }

    @Override
    public void createBookChapterTable(String tableName) {

        String createTable = "CREATE TABLE "+tableName+"(" +
                ChapterTable.Cols.TITLE+" text, "+
                ChapterTable.Cols.LINK+" text, "+
                ChapterTable.Cols.CACHE+" integer default 0, "+
                ChapterTable.Cols.BODY+" text"+
                ")";

        mSQLiteDatabase.execSQL(createTable);
    }

    @Override
    public List<Chapter> getChapters(String tableName) {
        List<Chapter> chapters = new ArrayList<>();
        Cursor cursor = mSQLiteDatabase.rawQuery("select title ,link ,cache from "+tableName,null);
        if (cursor.moveToFirst()){
            do{
                Chapter chapter = new Chapter();
                chapter.setTitle(cursor.getString(cursor.getColumnIndex(ChapterTable.Cols.TITLE)));
                chapter.setLink(cursor.getString(cursor.getColumnIndex(ChapterTable.Cols.LINK)));
                chapter.setCache(cursor.getInt(cursor.getColumnIndex(ChapterTable.Cols.CACHE)));
                chapters.add(chapter);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return chapters;
    }

    @Override
    public String getChapterBody(String tableName, String chapterTitle) {
        Cursor cursor = mSQLiteDatabase.rawQuery("select "+ChapterTable.Cols.BODY+" from "+tableName+
                " where "+ChapterTable.Cols.TITLE+" = ?",new String[]{chapterTitle});
        String body = null;
        if (cursor.moveToFirst()){
            body = cursor.getString(cursor.getColumnIndex(ChapterTable.Cols.BODY));
            Log.i(TAG, "getChapterBody: 查询到数据了");
        }
        Log.i(TAG, "getChapterBody: "+body);
        cursor.close();
        return body;
    }

    @Override
    public void addChapterBody(String tableName, String chapterTitle,String chapterBody) {
        mSQLiteDatabase.execSQL("update "+tableName+" set cache = 1, body = '"
                +chapterBody+"' where title = '"+chapterTitle+"'");
    }

    @Override
    public void addChapter(String tableName, Chapter chapter) {
        mSQLiteDatabase.execSQL("insert into "+tableName+"("+
                        ChapterTable.Cols.TITLE+", "+
                        ChapterTable.Cols.LINK +") values(?,?)",
                new Object[]{chapter.getTitle(),chapter.getLink()});
    }

    @Override
    public void addAllChapters(String tableName,List<Chapter> chapters) {
        for (Chapter chapter:chapters){
            mSQLiteDatabase.execSQL("insert into "+tableName+"("+
                    ChapterTable.Cols.TITLE+", "+
                    ChapterTable.Cols.LINK +") values(?,?)",
                    new Object[]{chapter.getTitle(),chapter.getLink()});
        }
    }

    @Override
    public boolean updateChapters(String tableName) {
        return false;
    }

    @Override
    public void deleteChapterTable(String tableName) {
        mSQLiteDatabase.execSQL("drop table "+tableName);
    }

}
