package com.example.bookyue.model;

import com.example.bookyue.database.bean.Chapter;

public class ChapterBodyJson {

    private boolean ok;
    private Chapter chapter;

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public boolean getOk() {
        return ok;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public Chapter getChapter() {
        return chapter;
    }
}
