package com.example.bookyue.fragment.bookshelf;

import android.content.Context;

import com.example.bookyue.database.bean.Book;

import java.util.List;

public interface IbookshelfView {
    Context getContext();
    void initRecyclerView(List<Book> books);
    void refreshRecyclerView();
    void showToast(int flag);
}
