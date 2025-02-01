package com.example.konyvtar.model;

public class Address {

    private int telepules_id;
    private String cim_utca;
    private String cim_hsz;

    public Address(int telepules_id, String cim_utca, String cim_hsz) {
        setTelepules_id(telepules_id);
        setCim_utca(cim_utca);
        setCim_hsz(cim_hsz);
    }

    public int getTelepules_id() {
        return telepules_id;
    }

    public void setTelepules_id(int telepules_id) {
        this.telepules_id = telepules_id;
    }

    public String getCim_utca() {
        return cim_utca;
    }

    public void setCim_utca(String cim_utca) {
        this.cim_utca = cim_utca;
    }

    public String getCim_hsz() {
        return cim_hsz;
    }

    public void setCim_hsz(String cim_hsz) {
        this.cim_hsz = cim_hsz;
    }

    @Override
    public String toString() {
        return "tag címe: " + getTelepules_id() + getCim_utca() + getCim_hsz();
    }
}
