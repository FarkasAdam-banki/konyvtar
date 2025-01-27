package com.example.konyvtar;

import com.example.konyvtar.input.TextInput;
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
    
    private TextInput tagKeresztnev, tagVezeteknev, tagOpcionalisNev, tagTelSzam, tagVaros, tagUtca, tagHazszam;
    private ConnectedTextInput tagNev;

    private Connection conn;
    private ObservableList<Megye> megyek;


    public void memberRegisztration() {
        String memberName = getMemberName();
        int telepules_id = -1;
        boolean ok = true;

        if (ok) {
            ok = false;
        } else if (tagTelSzam.isValid()) {
            ok = false;
        } else if (tagUtca.isValid()) {
            ok = false;
        } else if (tagVaros.isValid()) {
            ok = false;
            telepules_id = getTelepulesId(memberCity.getText());
            if (telepules_id == -1) {
                ok = false;
            }


        }
    }

    private String getMemberName() {
        return tagVezeteknev.getValue() + tagKeresztnev.getValue() + tagOpcionalisNev.getValue();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        megyek = FXCollections.observableArrayList();
        conn = DatabaseConnection.getConnection();
        megyeFeltoltes();
        tagKeresztnev = new TextInput(memberLastName);
        tagVezeteknev = new TextInput(memberFirstName);
        tagOpcionalisNev = new TextInput(memberOptionalName, true);
        tagTelSzam = new TextInput(memberPhoneNumber,12);
        tagVaros = new TextInput(memberCity,25);
        tagUtca = new TextInput(memberStreet, 30);
        tagHazszam = new TextInput(memberHouseNumber, 20);
        tagTelSzam.setRegex("^(\\+36|06)?\\s?(20|30|70)\\s?[0-9]{3}\\s?[0-9]{4}$");
        tagUtca.setRegex("^[0-9]+[a-zA-Z]?(/[a-zA-Z]|\\\\.[a-zA-Z])?$");

        

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
