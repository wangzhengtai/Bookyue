package com.example.bookyue.activity.read;

import android.util.Pair;

import com.example.bookyue.IPresenter;

interface IReadPresenter extends IPresenter{
    boolean haveThisBook();
    void initView();
    void refreshReadView();
    String getChapterTitle(int index);
    String[] getParagraphs(int index);
    Pair<Integer,Integer> getPair(int indexOfChapters,int index);
    int getChaptersSize();
    int getPages(int index);
    int getIndexOfChapters();
    int getIndexOfPages();
    void setIndexOfPages(int index);
    void setIndexOfChapters(int indexOfChapters);
    void increaseIndexOfChapters();
    void decrementIndexOfChapters();
    void loadPreChapterData();
    void loadNextChapterData();
    void saveReadProgress();
    void addBookToDatabase();
    void deleteBookDataFromDatabase();
}
