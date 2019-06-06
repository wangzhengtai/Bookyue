package com.example.bookyue.fragment.bookshelf;


import com.example.bookyue.database.bean.Book;

import java.util.List;

public interface IbookshelfModel {
    int refreshBooksFromNetwork();
    List<Book> getBooks();
    void updateBookState(int position,int isUpdate);
}
