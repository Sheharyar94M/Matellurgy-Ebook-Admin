package com.qsa.metallurgy_material_engineering_admin.Model;

public class postBook {
    String id, bookName, categoryName, authorName, description, pdfUrl, youtubeUrl, bookImage;

    String ratingBar;
    String liked;
    String shared;
    String readers;

    public postBook(){

    }

    public postBook(String id, String bookName, String authorName, String bookImage, String pdfUrl, String categoryName, String ratingBar, String description, String liked, String shared, String readers, String youtubeUrl) {

        this.id = id;
        this.bookName = bookName;
        this.authorName = authorName;
        this.bookImage = bookImage;
        this.pdfUrl = pdfUrl;
        this.categoryName = categoryName;
        this.ratingBar = ratingBar;
        this.description = description;
        this.liked = liked;
        this.shared = shared;
        this.readers = readers;
        this.youtubeUrl = youtubeUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getRatingBar() {
        return ratingBar;
    }

    public void setRatingBar(String ratingBar) {
        this.ratingBar = ratingBar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLiked() {
        return liked;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }

    public String getShared() {
        return shared;
    }

    public void setShared(String shared) {
        this.shared = shared;
    }

    public String getReaders() {
        return readers;
    }

    public void setReaders(String readers) {
        this.readers = readers;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }
}
