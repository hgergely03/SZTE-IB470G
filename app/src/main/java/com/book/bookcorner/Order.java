package com.book.bookcorner;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class Order {
    private String orderId;
    private String bookId;
    private String buyerEmail;
    private Date orderDate;

    public Order() {
    }

    public Order(String bookId, String buyerEmail, Date orderDate) {
        this.bookId = bookId;
        this.buyerEmail = buyerEmail;
        this.orderDate = orderDate;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Exclude
    public String getOrderId() {
        return orderId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public Date getOrderDate() {
        return orderDate;
    }
}
