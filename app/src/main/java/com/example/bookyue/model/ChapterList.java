package com.example.bookyue.model;


import java.util.Date;
import java.util.List;

public class ChapterList {

    private MixToc mixToc;
    private boolean ok;
    public void setMixToc(MixToc mixToc) {
        this.mixToc = mixToc;
    }
    public MixToc getMixToc() {
        return mixToc;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
    public boolean getOk() {
        return ok;
    }

    //我也不知道这是啥玩意，解析出来的json字符串中是这样的
    public static class MixToc {

        private String _id;
        private int chaptersCount1;
        private String book;
        private Date chaptersUpdated;
        private List<Chapter> chapters;
        private Date updated;
        public void set_id(String _id) {
            this._id = _id;
        }
        public String get_id() {
            return _id;
        }

        public void setChaptersCount1(int chaptersCount1) {
            this.chaptersCount1 = chaptersCount1;
        }
        public int getChaptersCount1() {
            return chaptersCount1;
        }

        public void setBook(String book) {
            this.book = book;
        }
        public String getBook() {
            return book;
        }

        public void setChaptersUpdated(Date chaptersUpdated) {
            this.chaptersUpdated = chaptersUpdated;
        }
        public Date getChaptersUpdated() {
            return chaptersUpdated;
        }

        public void setChapters(List<Chapter> chapters) {
            this.chapters = chapters;
        }
        public List<Chapter> getChapters() {
            return chapters;
        }

        public void setUpdated(Date updated) {
            this.updated = updated;
        }
        public Date getUpdated() {
            return updated;
        }

    }

    public static class Chapter extends com.example.bookyue.database.bean.Chapter {

        private boolean unreadble;

        public void setUnreadble(boolean unreadble) {
            this.unreadble = unreadble;
        }
        public boolean getUnreadble() {
            return unreadble;
        }
    }
}
