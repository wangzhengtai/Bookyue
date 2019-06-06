package com.example.bookyue.database.bean;

import android.support.annotation.NonNull;

public class Book {

    private String _id;
    private String title;
    private String cover;
    private String updated;
    private String lastChapter;
    private int indexOfChapters;           //记录当前的阅读章节索引，默认是0
    private int indexOfPages;              //记录阅读章节对应的页数，默认为0
    private int isSerial;           //是否连载
    private int isUpdate;           //是否更新   SQLite没有boolean

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public int getIndexOfChapters() {
        return indexOfChapters;
    }

    public void setIndexOfChapters(int indexOfChapters) {
        this.indexOfChapters = indexOfChapters;
    }

    public int getIndexOfPages() {
        return indexOfPages;
    }

    public void setIndexOfPages(int indexOfPages) {
        this.indexOfPages = indexOfPages;
    }

    public int getIsSerial() {
        return isSerial;
    }

    public void setIsSerial(int isSerial) {
        this.isSerial = isSerial;
    }

    public int getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(int update) {
        isUpdate = update;
    }

    @NonNull
    @Override
    public String toString() {
//        return "[ _id:"+_id+" title:"+title+" covet:"+cover+" updated:"+updated+
//                " lastChapter:"+lastChapter+" isUpdate:"+isUpdate+"]";
        return "[ title:"+title+" updated:"+updated+" lastChapter:"+lastChapter+" isUpdate:"+isUpdate+"]";
    }
}
