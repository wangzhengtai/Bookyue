package com.example.bookyue.database.dao;

import com.example.bookyue.database.bean.Chapter;

import java.util.List;

public interface IChapterDao {
    boolean hasThisTable(String tableName);
    void createBookChapterTable(String tableName);
    List<Chapter> getChapters(String tableName);
    String getChapterBody(String tableName,String chapterTitle);
    void addChapterBody(String tableName,String chapterTitle,String ChapterBody);
    void addChapter(String tableName,Chapter chapter);
    void addAllChapters(String tableName,List<Chapter> chapters);
    boolean updateChapters(String tableName);
    void deleteChapterTable(String tableName);
}
