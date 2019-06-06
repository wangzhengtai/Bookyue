package com.example.bookyue.activity.read;

import android.content.Context;

import com.example.bookyue.database.bean.Chapter;

import java.util.List;

public interface IReadView {
    Context getContext();
    void initChapterList(List<Chapter> chapters,int indexOfChapters);
    void initReadView();
    void refreshChapterList(List<Chapter> chapters);
    void refreshReadView();
}
