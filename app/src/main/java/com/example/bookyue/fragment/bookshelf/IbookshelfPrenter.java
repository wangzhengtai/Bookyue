package com.example.bookyue.fragment.bookshelf;

import com.example.bookyue.IPresenter;
import com.example.bookyue.database.bean.Book;

import java.util.List;

public interface IbookshelfPrenter extends IPresenter {
    void initView();
    void refreshView();
    List<Book> getBooks();
    void updateBookState(int position,int isUpdate);
}
