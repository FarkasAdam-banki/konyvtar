package com.example.konyvtar;

public class County {

    private int megye_id;
    private String megye_megnevezes;

    public County(int megye_id, String megye_megnevezes) {
       setMegye_id(megye_id);
       setMegye_megnevezes(megye_megnevezes);
    }

    public int getMegye_id() {
        return megye_id;
    }

    public void setMegye_id(int megye_id) {
        this.megye_id = megye_id;
    }

    public String getMegye_megnevezes() {
        return megye_megnevezes;
    }

    public void setMegye_megnevezes(String megye_megnevezes) {
        this.megye_megnevezes = megye_megnevezes;
    }

    @Override
    public String toString() {
        return this.getMegye_megnevezes();
    }
}
