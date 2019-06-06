package com.example.bookyue.database;

//数据库定义模板
public class DatabaseSchema {

    public static final String DATABASE_NAME = "Bookyue.db";

    //Book表
    public static final class BookTable{
        public static final String NAME = "Book";

        public static final class Cols{
            public static final String ID = "_id";
            public static final String TITLE = "title";
            public static final String COVER = "cover";
            public static final String UPDATED = "updated";
            public static final String LAST_CHAPTER = "lastChapter";
            public static final String INDEX_OF_CHAPTERS = "indexOfChapters";
            public static final String INDEX_OF_PAGES = "indexOfPages";
            public static final String IS_SERIAL = "isSerial";
            public static final String IS_UPDATE = "isUpdate";
        }
    }

    //书籍章节表 每本书有一个章节表 表名为书名
    public static final class ChapterTable{
        //表名为书名

        public static final class Cols{
            public static final String TITLE = "title";
            public static final String LINK = "link";
            public static final String CACHE = "cache";
            public static final String BODY = "body";
        }
    }
}
