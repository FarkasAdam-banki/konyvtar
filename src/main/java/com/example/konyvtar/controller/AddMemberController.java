package com.example.konyvtar.controller;

import com.example.konyvtar.DatabaseConnection;
import com.example.konyvtar.input.*;
import com.example.konyvtar.model.County;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

public class AddMemberController implements Initializable {

    @FXML
    private TextField memberId, memberLastName, memberFirstName, memberOptionalName, memberPhoneNumber, memberCity, memberStreet, memberHouseNumber;
    @FXML
    private ComboBox<County> memberCounty;
    @FXML
    private Label errorMessage, successMessage;

    private Select<County> countySelect;
    private TextInput firstNameInput, lastNameInput, optionalNameInput, phoneNumberInput, cityInput, streetInput, houseNumberInput, idInput;
    private ConnectedTextInput memberName, memberAddress;

    private List<Input> inputs;


    public void memberRegisztration() {
        int telepules_id = -1;
        boolean ok = true;

        int i = 0;
        while (i < inputs.size() && inputs.get(i).isValidOrFail()) {
            i++;
        }
        if (i != inputs.size()) {
            ok = false;
        }

        telepules_id = getTelepulesId(memberCity.getText());
        if (ok && telepules_id == -1) {
            errorMessage.setText("Nem létező település!");
            ok = false;
        }
        if (ok) {
            PreparedStatement pstmt;
            Alert alert;
            String addressSQL = "INSERT INTO cim (telepules_id, cim_utca, cim_hsz) VALUES (?, ?, ?)";
            String memberSQL = "INSERT INTO tag (tag_id, tag_nev, cim_id, tag_tel) VALUES (?, ?, (SELECT cim_id FROM cim ORDER BY cim_id DESC LIMIT 1) , ?)";
            try {
                pstmt = DatabaseConnection.getPreparedStatement(addressSQL);
                pstmt.setString(1, String.valueOf(telepules_id));
                pstmt.setString(2, streetInput.getValue());
                pstmt.setString(3, houseNumberInput.getValue());
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = DatabaseConnection.getPreparedStatement(memberSQL);
                pstmt.setString(1, idInput.getValue());
                pstmt.setString(2, memberName.getValue());
                pstmt.setString(3, phoneNumberInput.getValue());
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Tag Regisztrálása");
                alert.setContentText("Tag neve: " + memberName.getValue() + "\nCíme: " + memberAddress.getValue() + "\nTelefon száma: " + phoneNumberInput.getValue());
                alert.show();
                errorMessage.setText("");
                successMessage.setText("Tag sikeresen regisztrálva!");
                for (Input j : inputs) {
                    j.reset();
                }
                idInput.reset();
                pstmt.executeUpdate();
                pstmt.close();
            } catch (SQLException sqle) {
                System.err.println(sqle.getMessage());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countySelect = new Select<>(memberCounty, false);
        countySelect.setOnValidationFail(_ -> errorMessage.setText("Válassza ki a megyét!"));
        loadCounties();
        idInput = new TextInput(memberId, 9);
        idInput.setOnValidationFail(_ -> errorMessage.setText("Generáljon egy tagsági azonosítót!"));
        firstNameInput = new TextInput(memberFirstName);
        lastNameInput = new TextInput(memberLastName);
        optionalNameInput = new TextInput(memberOptionalName, true);
        phoneNumberInput = new TextInput(memberPhoneNumber, 12);
        phoneNumberInput.setRegex("^(\\+36|06)?\\s?(20|30|70)\\s?[0-9]{3}\\s?[0-9]{4}$");
        phoneNumberInput.setOnValidationFail(validationResult -> {
            if (validationResult == ValidationResult.REGEX_FAIL) {
                errorMessage.setText("Rossz formátum! A telefon szám formátuma: +3620234567 vagy 06201234567.");
            } else {
                errorMessage.setText("Hibás telefon szám!");
            }
        });
        cityInput = new TextInput(memberCity, 25);
        cityInput.setOnValidationFail(validationResult -> {
            errorMessage.setText(getErrorMessageTextInput(validationResult) + "a város név!");
        });
        streetInput = new TextInput(memberStreet, 30);
        streetInput.setMinLength(1);
        streetInput.setOnValidationFail(validationResult -> {
            errorMessage.setText(getErrorMessageTextInput(validationResult) + "az utca név!");
        });
        houseNumberInput = new TextInput(memberHouseNumber, 20);
        houseNumberInput.setMinLength(1);
        houseNumberInput.setRegex("^[0-9]+[a-zA-Z]?(/[a-zA-Z]|\\\\.[a-zA-Z])?$");
        houseNumberInput.setOnValidationFail(validationResult -> {
            if (validationResult == ValidationResult.REGEX_FAIL) {
                errorMessage.setText("Hibás formátum!");
            } else {
                errorMessage.setText("Hibás ház szám!");
            }
        });
        memberName = new ConnectedTextInput();
        memberName.addInput(lastNameInput);
        memberName.addInput(firstNameInput);
        memberName.addInput(optionalNameInput);
        memberName.setOnValidationFail(validationResult -> {
            errorMessage.setText(getErrorMessageTextInput(validationResult) + "a név!");
        });
        memberAddress = new ConnectedTextInput();
        memberAddress.addInput(cityInput);
        memberAddress.addInput(streetInput);
        memberAddress.addInput(houseNumberInput);

        inputs = List.of(memberName, idInput, phoneNumberInput, cityInput, countySelect, streetInput, houseNumberInput);
    }

    public boolean isSameId(String randomId) {
        Statement stmt = null;
        Connection conn = DatabaseConnection.getConnection();
        boolean same = false;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT tag_id FROM tag");
            while (!same && rs.next()) {
                int id = rs.getInt("tag_id");
                same = randomId.equalsIgnoreCase(String.valueOf(id));
            }
            rs.close();
            stmt.close();
        } catch (SQLException sqle) {
            sqle.getMessage();
        }
        return same;
    }

    public void generateRandomId() {
        String randomId = randomMemberId(idInput.getMaxLength());
        if (!isSameId(randomId)) idInput.setValue(randomId);
    }

    public String randomMemberId(int max) {
        String tagId = String.valueOf((int) (Math.random() * 9) + 1);
        for (int i = 0; i < max - 1; i++) {
            tagId += (int) (Math.random() * 10);
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

    public void loadCounties() {
        Statement stmt;
        Connection conn = DatabaseConnection.getConnection();
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM megye");
            while (rs.next()) {
                int id = rs.getInt("megye_id");
                String megye_megnevezese = rs.getString("megye_megnevezese");
                County m = new County(id, megye_megnevezese);
                countySelect.addOption(m);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public int getTelepulesId(String telepules) {
        PreparedStatement stmt = null;
        try {
            stmt = DatabaseConnection.getPreparedStatement("SELECT telepules_id FROM telepules WHERE telepules_megnevezese=?");
            stmt.setString(1, telepules);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("telepules_id");
                rs.close();
                stmt.close();
                return id;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

}
