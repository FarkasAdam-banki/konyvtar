package com.example.konyvtar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class TagRegController implements Initializable {

    @FXML
    private TextField memberLastName, memberFirstName, memberOptionalName, memberPhoneNumber, memberCity, memberStreet, memberHouseNumber;
    @FXML
    private ComboBox<Megye> memberCounty;

    private Connection conn;
    private ObservableList<Megye> megyek;


    public void memberRegisztration() {
        String memberName = memberFirstName.getText() + memberLastName.getText() + memberOptionalName.getText();
        String mPhoneNumber = memberPhoneNumber.getText();
        int telepules_id = -1;
        String mStreet = memberStreet.getText();
        String mHouseNumber = memberHouseNumber.getText();
        boolean ok;

        if (!isCorrectData(memberName, 35)) {
            ok = false;
        } else if (mPhoneNumber.matches("^(\\+36|06)?\\s?(20|30|70)\\s?[0-9]{3}\\s?[0-9]{4}$")) {
            ok = false;
        } else if (isCorrectData(mStreet, 30)) {
            ok = false;
        } else if (mStreet.matches("^[0-9]+[a-zA-Z]?(/[a-zA-Z]|\\\\.[a-zA-Z])?$")) {
            
        } else {
            if (!isCorrectData(memberCity.getText(), 25)) {
                ok = false;
            } else {
                telepules_id = getTelepulesId(memberCity.getText());
                if (telepules_id == -1) {
                    ok = false;
                }
            }
        }
    }


    public boolean isString(String data) {
        if (!Character.isUpperCase(data.charAt(0))) {
            return false;
        }
        for (int i = 1; i < data.length(); i++) {
            if (!Character.isLowerCase(data.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean isCorrectData(String data, int size) {
        data = data.trim();
        if (data.isEmpty() || data.length() > size) {
            return false;
        }
        String reszek[] = data.split(" ");
        for (int i = 0; i < reszek.length; i++) {
            if (!isString(reszek[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        megyek = FXCollections.observableArrayList();
        conn = DatabaseConnection.getConnection();
        megyeFeltoltes();

        memberCounty.setItems(megyek);
    }

    public void megyeFeltoltes() {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM megye");
            while (rs.next()) {
                int id = rs.getInt("megye_id");
                String megye = rs.getString("megye_megnevezese");
                Megye m = new Megye(id, megye);
                megyek.add(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getTelepulesId(String telepules) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT telepules_id FROM telepules WHERE telepules_megnevezese=?");
            stmt.setString(1, telepules);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("telepules_id");
                rs.close();
                return id;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

}
