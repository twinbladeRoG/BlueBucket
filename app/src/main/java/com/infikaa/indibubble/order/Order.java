package com.infikaa.indibubble.order;

/**
 * Created by Sohan on 13-Apr-17.
 */

public class Order {
    private String productName;
    private int quantity;
    private int productPrice;
    private String offerDescription;
    private int totalPrice;
    private boolean atHalt;
    private boolean atProcess;
    private boolean atComplete;
    private String orderID;
    private String paymentType;
    private String address;
    private String imageLink;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isAtHalt() {
        return atHalt;
    }

    public void setAtHalt(boolean atHalt) {
        this.atHalt = atHalt;
    }

    public boolean isAtProcess() {
        return atProcess;
    }

    public void setAtProcess(boolean atProcess) {
        this.atProcess = atProcess;
    }

    public boolean isAtComplete() {
        return atComplete;
    }

    public void setAtComplete(boolean atComplete) {
        this.atComplete = atComplete;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
