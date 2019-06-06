package com.example.bookyue.activity.BookDetail;

import com.example.bookyue.IPresenter;

public interface IBookDetailPresenter extends IPresenter {
    void initView();
    boolean haveThisBook();
    String getBookId();
    String getBookTitle();
    void addBookToDatabase();
    void deleteBookDataFromDatabase();
}
