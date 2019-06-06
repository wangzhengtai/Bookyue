package com.example.bookyue.model;

import java.util.Date;
import java.util.List;

public class SingleRanking {

    private Ranking ranking;
    private boolean ok;

    public void setRanking(Ranking ranking) {
        this.ranking = ranking;
    }
    public Ranking getRanking() {
        return ranking;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
    public boolean getOk() {
        return ok;
    }


    public static class Ranking {

        private String _id;
        private Date updated;
        private String title;
        private String tag;
        private String cover;
        private String icon;
        private int __v;
        private String monthRank;
        private String totalRank;
        private String shortTitle;
        private Date created;
        private String biTag;
        private boolean isSub;
        private boolean collapse;
        private boolean _new;
        private String gender;
        private int priority;
        private List<Book> books;
        private String id;
        private int total;
        public void set_id(String _id) {
            this._id = _id;
        }
        public String get_id() {
            return _id;
        }

        public void setUpdated(Date updated) {
            this.updated = updated;
        }
        public Date getUpdated() {
            return updated;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }
        public String getCover() {
            return cover;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
        public String getIcon() {
            return icon;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }
        public int get__v() {
            return __v;
        }

        public void setMonthRank(String monthRank) {
            this.monthRank = monthRank;
        }
        public String getMonthRank() {
            return monthRank;
        }

        public void setTotalRank(String totalRank) {
            this.totalRank = totalRank;
        }
        public String getTotalRank() {
            return totalRank;
        }

        public void setShortTitle(String shortTitle) {
            this.shortTitle = shortTitle;
        }
        public String getShortTitle() {
            return shortTitle;
        }

        public void setCreated(Date created) {
            this.created = created;
        }
        public Date getCreated() {
            return created;
        }

        public void setBiTag(String biTag) {
            this.biTag = biTag;
        }
        public String getBiTag() {
            return biTag;
        }

        public void setIsSub(boolean isSub) {
            this.isSub = isSub;
        }
        public boolean getIsSub() {
            return isSub;
        }

        public void setCollapse(boolean collapse) {
            this.collapse = collapse;
        }
        public boolean getCollapse() {
            return collapse;
        }

        public void set_new(boolean _new) {
            this._new = _new;
        }
        public boolean get_new() {
            return _new;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
        public String getGender() {
            return gender;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }
        public int getPriority() {
            return priority;
        }

        public void setBooks(List<Book> books) {
            this.books = books;
        }
        public List<Book> getBooks() {
            return books;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setTotal(int total) {
            this.total = total;
        }
        public int getTotal() {
            return total;
        }

    }

    public static class Book {

        private String _id;
        private String title;
        private String majorCate;
        private String shortIntro;
        private String minorCate;
        private String site;
        private String author;
        private String cover;
        private boolean allowMonthly;
        private int banned;
        private long latelyFollower;
        private String retentionRatio;

        public void set_id(String _id) {
            this._id = _id;
        }
        public String get_id() {
            return _id;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setMajorCate(String majorCate) {
            this.majorCate = majorCate;
        }
        public String getMajorCate() {
            return majorCate;
        }

        public void setShortIntro(String shortIntro) {
            this.shortIntro = shortIntro;
        }
        public String getShortIntro() {
            return shortIntro;
        }

        public void setMinorCate(String minorCate) {
            this.minorCate = minorCate;
        }
        public String getMinorCate() {
            return minorCate;
        }

        public void setSite(String site) {
            this.site = site;
        }
        public String getSite() {
            return site;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
        public String getAuthor() {
            return author;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }
        public String getCover() {
            return cover;
        }

        public void setAllowMonthly(boolean allowMonthly) {
            this.allowMonthly = allowMonthly;
        }
        public boolean getAllowMonthly() {
            return allowMonthly;
        }

        public void setBanned(int banned) {
            this.banned = banned;
        }
        public int getBanned() {
            return banned;
        }

        public void setLatelyFollower(long latelyFollower) {
            this.latelyFollower = latelyFollower;
        }
        public long getLatelyFollower() {
            return latelyFollower;
        }

        public void setRetentionRatio(String retentionRatio) {
            this.retentionRatio = retentionRatio;
        }
        public String getRetentionRatio() {
            return retentionRatio;
        }

    }
}
