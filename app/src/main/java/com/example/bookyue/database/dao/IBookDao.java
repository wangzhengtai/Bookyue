package com.example.bookyue.database.dao;

import android.util.Pair;

import com.example.bookyue.database.bean.Book;
import com.example.bookyue.model.BookDetail;

import java.util.List;

public interface IBookDao {
    boolean haveThisBook(String bookId);
    void addBook(BookDetail bookDetail);
    void getBooks(List<Book> books);
    void updateBook(BookDetail bookDetail);
    void updateBookState(String _id,int isUpdate);
    Pair<Integer,Integer> getReadProgress(String bookId);
    void saveReadProgress(String bookId,int indexOfChapters,int indexOfPages);
    void deleteBook(String bookId);
}
