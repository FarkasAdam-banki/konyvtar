package com.example.konyvtar;

public class Month{
    private int monthId;
    private String monthName;

    public Month(int honap_id, String honap_megnevezes) {
        this.setMonthId(honap_id);
        this.setMonthName(honap_megnevezes);
    }

    public int getHonap_id() {
        return this.monthId;
    }

    public void setMonthId(int monthId) {
        this.monthId = monthId;
    }

    public String getMonthName() {
        return this.monthName;
    }

    public String toString() {
        return this.getMonthName();
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }
}
