package com.example.konyvtar.model;



public class Member {
    private int id;
    private String name;
    private String address;
    private String phone;
    private int borrowedBooks;
    private int delay;

    public Member(int id, String name, String address, String phone, int borrowedBooks, int delay) {
        setId(id);
        setName(name);
        setAddress(address);
        setPhone(phone);
        setBorrowedBooks(borrowedBooks);
        setDelay(delay);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBorrowedBooks(int borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public int getBorrowedBooks() {
        return borrowedBooks;
    }

    public int getDelay() {
        return delay;
    }
    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", borrowedBooks=" + borrowedBooks +
                ", delay=" + delay +
                '}';
    }

}
