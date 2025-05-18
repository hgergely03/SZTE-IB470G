package com.book.bookcorner;

import java.util.Date;

public class Order {
    private String orderId;
    private String bookId;
    private String buyerEmail;
    private Date orderDate;

    public Order(String orderId, String bookId, String buyerEmail, Date orderDate) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.buyerEmail = buyerEmail;
        this.orderDate = orderDate;
    }

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
