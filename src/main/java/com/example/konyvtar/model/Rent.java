package com.example.konyvtar.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Rent {


    private int rentId;
    private String membershipId;
    private String serialNumber;
    private String borrowDate;
    private String dueDate;
    public Rent(int rentId, String membershipId, String serialNumber, String borrowDate, String dueDate, boolean returned) {
        setRentId(rentId);
        setMembershipId(membershipId);
        setSerialNumber(serialNumber);
        setBorrowDate(borrowDate);
        setDueDate(dueDate);

        this.membershipId = membershipId;
        this.serialNumber = serialNumber;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returned = new SimpleBooleanProperty(returned);
    }

    public int getRentId() {
        return rentId;
    }

    public void setRentId(int rentId) {
        this.rentId = rentId;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    private BooleanProperty returned;

    public boolean isReturned() {
        return returned.get();
    }

    public void setReturned(BooleanProperty returned){
        this.returned = returned;
    }

    public BooleanProperty returnedProperty() {
        return returned;
    }
}