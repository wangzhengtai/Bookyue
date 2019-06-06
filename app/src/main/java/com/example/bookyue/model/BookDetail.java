package com.example.bookyue.model;

import java.util.List;
import java.util.Date;

public class BookDetail {

    private String _id;
    private String longIntro;
    private String majorCateV2;
    private String author;
    private String minorCateV2;
    private String majorCate;
    private String minorCate;
    private String title;
    private String cover;
    private String creater;
    private boolean isMakeMoneyLimit;
    private boolean isFineBook;
    private int safelevel;
    private boolean allowFree;
    private String originalAuthor;
    private List<String> anchors;
    private String authorDesc;
    private Rating rating;
    private boolean hasCopyright;
    private int buytype;
    private int sizetype;
    private String superscript;
    private int currency;
    private String contentType;
    private boolean _le;
    private boolean allowMonthly;
    private boolean allowVoucher;
    private boolean allowBeanVoucher;
    private boolean hasCp;
    private int banned;
    private long postCount;
    private long latelyFollower;
    private int followerCount;
    private long wordCount;
    private int serializeWordCount;
    private String retentionRatio;
    private String updated;
    private boolean isSerial;
    private int chaptersCount;
    private String lastChapter;
    private List<String> gender;
    private List<String> tags;
    private boolean advertRead;
    private String cat;
    private boolean donate;
    private String copyright;
    private boolean _gg;
    private boolean isForbidForFreeApp;
    private boolean isAllowNetSearch;
    private boolean limit;
    private String copyrightDesc;
    private String discount;

    public void set_id(String _id) {
        this._id = _id;
    }
    public String get_id() {
        return _id;
    }

    public void setLongIntro(String longIntro) {
        this.longIntro = longIntro;
    }
    public String getLongIntro() {
        return longIntro;
    }

    public void setMajorCateV2(String majorCateV2) {
        this.majorCateV2 = majorCateV2;
    }
    public String getMajorCateV2() {
        return majorCateV2;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public String getAuthor() {
        return author;
    }

    public void setMinorCateV2(String minorCateV2) {
        this.minorCateV2 = minorCateV2;
    }
    public String getMinorCateV2() {
        return minorCateV2;
    }

    public void setMajorCate(String majorCate) {
        this.majorCate = majorCate;
    }
    public String getMajorCate() {
        return majorCate;
    }

    public void setMinorCate(String minorCate) {
        this.minorCate = minorCate;
    }
    public String getMinorCate() {
        return minorCate;
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

    public void setCreater(String creater) {
        this.creater = creater;
    }
    public String getCreater() {
        return creater;
    }

    public void setIsMakeMoneyLimit(boolean isMakeMoneyLimit) {
        this.isMakeMoneyLimit = isMakeMoneyLimit;
    }
    public boolean getIsMakeMoneyLimit() {
        return isMakeMoneyLimit;
    }

    public void setIsFineBook(boolean isFineBook) {
        this.isFineBook = isFineBook;
    }
    public boolean getIsFineBook() {
        return isFineBook;
    }

    public void setSafelevel(int safelevel) {
        this.safelevel = safelevel;
    }
    public int getSafelevel() {
        return safelevel;
    }

    public void setAllowFree(boolean allowFree) {
        this.allowFree = allowFree;
    }
    public boolean getAllowFree() {
        return allowFree;
    }

    public void setOriginalAuthor(String originalAuthor) {
        this.originalAuthor = originalAuthor;
    }
    public String getOriginalAuthor() {
        return originalAuthor;
    }

    public void setAnchors(List<String> anchors) {
        this.anchors = anchors;
    }
    public List<String> getAnchors() {
        return anchors;
    }

    public void setAuthorDesc(String authorDesc) {
        this.authorDesc = authorDesc;
    }
    public String getAuthorDesc() {
        return authorDesc;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }
    public Rating getRating() {
        return rating;
    }

    public void setHasCopyright(boolean hasCopyright) {
        this.hasCopyright = hasCopyright;
    }
    public boolean getHasCopyright() {
        return hasCopyright;
    }

    public void setBuytype(int buytype) {
        this.buytype = buytype;
    }
    public int getBuytype() {
        return buytype;
    }

    public void setSizetype(int sizetype) {
        this.sizetype = sizetype;
    }
    public int getSizetype() {
        return sizetype;
    }

    public void setSuperscript(String superscript) {
        this.superscript = superscript;
    }
    public String getSuperscript() {
        return superscript;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }
    public int getCurrency() {
        return currency;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public String getContentType() {
        return contentType;
    }

    public void set_le(boolean _le) {
        this._le = _le;
    }
    public boolean get_le() {
        return _le;
    }

    public void setAllowMonthly(boolean allowMonthly) {
        this.allowMonthly = allowMonthly;
    }
    public boolean getAllowMonthly() {
        return allowMonthly;
    }

    public void setAllowVoucher(boolean allowVoucher) {
        this.allowVoucher = allowVoucher;
    }
    public boolean getAllowVoucher() {
        return allowVoucher;
    }

    public void setAllowBeanVoucher(boolean allowBeanVoucher) {
        this.allowBeanVoucher = allowBeanVoucher;
    }
    public boolean getAllowBeanVoucher() {
        return allowBeanVoucher;
    }

    public void setHasCp(boolean hasCp) {
        this.hasCp = hasCp;
    }
    public boolean getHasCp() {
        return hasCp;
    }

    public void setBanned(int banned) {
        this.banned = banned;
    }
    public int getBanned() {
        return banned;
    }

    public void setPostCount(long postCount) {
        this.postCount = postCount;
    }
    public long getPostCount() {
        return postCount;
    }

    public void setLatelyFollower(long latelyFollower) {
        this.latelyFollower = latelyFollower;
    }
    public long getLatelyFollower() {
        return latelyFollower;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }
    public int getFollowerCount() {
        return followerCount;
    }

    public void setWordCount(long wordCount) {
        this.wordCount = wordCount;
    }
    public long getWordCount() {
        return wordCount;
    }

    public void setSerializeWordCount(int serializeWordCount) {
        this.serializeWordCount = serializeWordCount;
    }
    public int getSerializeWordCount() {
        return serializeWordCount;
    }

    public void setRetentionRatio(String retentionRatio) {
        this.retentionRatio = retentionRatio;
    }
    public String getRetentionRatio() {
        return retentionRatio;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
    public String getUpdated() {
        return updated;
    }

    public void setIsSerial(boolean isSerial) {
        this.isSerial = isSerial;
    }
    public boolean getIsSerial() {
        return isSerial;
    }

    public void setChaptersCount(int chaptersCount) {
        this.chaptersCount = chaptersCount;
    }
    public int getChaptersCount() {
        return chaptersCount;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }
    public String getLastChapter() {
        return lastChapter;
    }

    public void setGender(List<String> gender) {
        this.gender = gender;
    }
    public List<String> getGender() {
        return gender;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public List<String> getTags() {
        return tags;
    }

    public void setAdvertRead(boolean advertRead) {
        this.advertRead = advertRead;
    }
    public boolean getAdvertRead() {
        return advertRead;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }
    public String getCat() {
        return cat;
    }

    public void setDonate(boolean donate) {
        this.donate = donate;
    }
    public boolean getDonate() {
        return donate;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
    public String getCopyright() {
        return copyright;
    }

    public void set_gg(boolean _gg) {
        this._gg = _gg;
    }
    public boolean get_gg() {
        return _gg;
    }

    public void setIsForbidForFreeApp(boolean isForbidForFreeApp) {
        this.isForbidForFreeApp = isForbidForFreeApp;
    }
    public boolean getIsForbidForFreeApp() {
        return isForbidForFreeApp;
    }

    public void setIsAllowNetSearch(boolean isAllowNetSearch) {
        this.isAllowNetSearch = isAllowNetSearch;
    }
    public boolean getIsAllowNetSearch() {
        return isAllowNetSearch;
    }

    public void setLimit(boolean limit) {
        this.limit = limit;
    }
    public boolean getLimit() {
        return limit;
    }

    public void setCopyrightDesc(String copyrightDesc) {
        this.copyrightDesc = copyrightDesc;
    }
    public String getCopyrightDesc() {
        return copyrightDesc;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
    public String getDiscount() {
        return discount;
    }

    private class Rating {

        private int count;
        private double score;
        private boolean isEffect;

        public void setCount(int count) {
            this.count = count;
        }
        public int getCount() {
            return count;
        }

        public void setScore(double score) {
            this.score = score;
        }
        public double getScore() {
            return score;
        }

        public void setIsEffect(boolean isEffect) {
            this.isEffect = isEffect;
        }
        public boolean getIsEffect() {
            return isEffect;
        }

    }

}
