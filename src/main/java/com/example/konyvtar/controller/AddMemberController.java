package com.example.konyvtar.controller;

import com.example.konyvtar.DatabaseConnection;
import com.example.konyvtar.input.ConnectedTextInput;
import com.example.konyvtar.input.Select;
import com.example.konyvtar.input.TextInput;
import com.example.konyvtar.input.ValidationResult;
import com.example.konyvtar.model.County;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AddMemberController implements Initializable {

    @FXML
    private TextField memeberId, memberLastName, memberFirstName, memberOptionalName, memberPhoneNumber, memberCity, memberStreet, memberHouseNumber;
    @FXML
    private ComboBox<County> memberCounty;
    @FXML
    private Label errorMessage, successMessage;

    private Select<County> megyeSelect;
    private TextInput tagKeresztnev, tagVezeteknev, tagOpcionalisNev, tagTelSzam, tagVaros, tagUtca, tagHazszam, tagId;
    private ConnectedTextInput tagNev, tagCim;

    private Connection conn;


    public void memberRegisztration() {
        int telepules_id = -1;
        boolean ok = true;

        if (!tagNev.isValidOrFail()) {
            ok = false;
        } else if (!tagTelSzam.isValidOrFail()) {
            ok = false;
        } else if (!tagUtca.isValidOrFail()) {
            ok = false;
        } else if (!tagVaros.isValidOrFail()) {
            ok = false;
        } else if (!tagHazszam.isValidOrFail()) {
            ok = false;
        }
        telepules_id = getTelepulesId(memberCity.getText());
        if (telepules_id == -1) {
            errorMessage.setText("Nem létező település!");
            ok = false;
        }
        if(ok){
            PreparedStatement pstmt;
            Alert alert;
            String cimsql = "INSERT INTO cim (telepules_id, cim_utca, cim_hsz) VALUES (?, ?, ?)";
            String tagsql = "INSERT INTO tag (tag_id, tag_nev, cim_id, tag_tel) VALUES (?, ?, (SELECT cim_id FROM cim ORDER BY cim_id DESC LIMIT 1) , ?)";
            try{
                pstmt = conn.prepareStatement(cimsql);
                pstmt.setString(1, String.valueOf(telepules_id));
                pstmt.setString(2,tagUtca.getValue());
                pstmt.setString(3,tagHazszam.getValue());
                pstmt.executeUpdate();
                pstmt = conn.prepareStatement(tagsql);
                pstmt.setString(1, tagId.getValue());
                pstmt.setString(2, tagNev.getValue());
                pstmt.setString(3, tagTelSzam.getValue());
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Tag Regisztrálása");
                alert.setContentText("Tag neve: " + tagNev.getValue() + "\nCíme: " + tagCim.getValue() + "\nTelefon száma: " + tagTelSzam.getValue());
                alert.show();
                errorMessage.setText("");
                successMessage.setText("Tag sikersen regisztrálva!");
                pstmt.executeUpdate();
            }catch (SQLException sqle){
                System.err.println(sqle.getMessage());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        megyeSelect = new Select<>(memberCounty, true);
        conn = DatabaseConnection.getConnection();
        megyeFeltoltes();
        tagId = new TextInput(memeberId, 9);
        tagKeresztnev = new TextInput(memberFirstName);
        tagVezeteknev = new TextInput(memberLastName);
        tagOpcionalisNev = new TextInput(memberOptionalName, true);
        tagTelSzam = new TextInput(memberPhoneNumber,12);
        tagTelSzam.setRegex("^(\\+36|06)?\\s?(20|30|70)\\s?[0-9]{3}\\s?[0-9]{4}$");
        tagTelSzam.setOnValidationFail(validationResult -> {
            if(validationResult == ValidationResult.REGEX_FAIL) {
                errorMessage.setText("Rossz formátum! A telefon szám formátuma: +3620234567 vagy 06201234567.");
            }
            else{
                errorMessage.setText("Hibás telefon szám!");
            }
        });
        tagVaros = new TextInput(memberCity,25);
        tagVaros.setOnValidationFail(validationResult -> {
            errorMessage.setText(getErrorMessageTextInput(validationResult)+"a város név!");
        });
        tagUtca = new TextInput(memberStreet, 30);
        tagUtca.setOnValidationFail(validationResult -> {
            errorMessage.setText(getErrorMessageTextInput(validationResult)+"a utca név!");
        });
        tagHazszam = new TextInput(memberHouseNumber, 20);
        tagHazszam.setRegex("^[0-9]+[a-zA-Z]?(/[a-zA-Z]|\\\\.[a-zA-Z])?$");
        tagHazszam.setOnValidationFail(validationResult -> {
            if(validationResult == ValidationResult.REGEX_FAIL) {
                errorMessage.setText("Hibás formátum!");
            }
            else{
                errorMessage.setText("Hibás ház szám!");
            }
        });
        tagNev = new ConnectedTextInput();
        tagNev.addInput(tagVezeteknev);
        tagNev.addInput(tagKeresztnev);
        tagNev.addInput(tagOpcionalisNev);
        tagNev.setOnValidationFail(validationResult -> {
            errorMessage.setText(getErrorMessageTextInput(validationResult)+"a név!");
        });
        tagCim = new ConnectedTextInput();
        tagCim.addInput(tagVaros);
        tagCim.addInput(tagUtca);
        tagCim.addInput(tagHazszam);

    }

    public boolean isSameId(String randomId){
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT tag_id FROM tag");
            while (rs.next()) {
                int id = rs.getInt("tag_id");
                return randomId.equalsIgnoreCase(String.valueOf(id));
            }

        }catch (SQLException sqle){
            sqle.getMessage();
        }
        return false;
    }

    public void generateRandomId(){
        String randomId = randomMemberId(tagId.getMaxLength());
        if(!isSameId(randomId)) tagId.setValue(randomId);
    }
    public String randomMemberId(int max){
        String tagId =String.valueOf((int)(Math.random()*9)+1);
        for(int i = 0; i < max-1; i++) {
            tagId += (int)(Math.random() * 10);
        }
        return tagId;
    }
    private String getErrorMessageTextInput(ValidationResult result) {
        return switch (result) {
            case EMPTY -> "Nincs megadva ";
            case TOO_SHORT -> "Túl rövid ";
            case TOO_LONG -> "Túl hosszú ";
            case REGEX_FAIL -> "Helytelen formátumú ";
            default -> throw new IllegalStateException("Unexpected value: " + result);
        };
    }

    public void megyeFeltoltes() {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM megye");
            while (rs.next()) {
                int id = rs.getInt("megye_id");
                String megye = rs.getString("megye_megnevezese");
                County m = new County(id, megye);
                megyeSelect.addOption(m);
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
