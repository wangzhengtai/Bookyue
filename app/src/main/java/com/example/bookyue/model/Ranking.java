package com.example.bookyue.model;

import java.util.List;

public class Ranking {

    private List<Item> male;
    private List<Item> female;
    private List<Item> picture;
    private List<Item> epub;
    private boolean ok;

    public List<Item> getMale() {
        return male;
    }

    public void setMale(List<Item> male) {
        this.male = male;
    }

    public List<Item> getFemale() {
        return female;
    }

    public void setFemale(List<Item> female) {
        this.female = female;
    }

    public List<Item> getPicture() {
        return picture;
    }

    public void setPicture(List<Item> picture) {
        this.picture = picture;
    }

    public List<Item> getEpub() {
        return epub;
    }

    public void setEpub(List<Item> epub) {
        this.epub = epub;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public static class Item {

        private String _id;
        private String title;
        private String cover;
        private boolean collapse;
        private String monthRank;
        private String totalRank;
        private String shortTitle;
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

        public void setCover(String cover) {
            this.cover = cover;
        }
        public String getCover() {
            return cover;
        }

        public void setCollapse(boolean collapse) {
            this.collapse = collapse;
        }
        public boolean getCollapse() {
            return collapse;
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

    }
}
