package com.book.bookcorner;

public class Book {
    private String id;
    private String title;
    private String author;
    private String description;
    private String imgUrl;
    private int price;
    private int boughtNumber;

    public Book() {
    }

    public Book(String title, String author, String description, String imgUrl, int price, int boughtNumber) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.imgUrl = imgUrl;
        this.price = price;
        this.boughtNumber = boughtNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getPrice() {
        return price;
    }

    public int getBoughtNumber() {
        return boughtNumber;
    }
}
