package com.example.konyvtar.controller;

import com.example.konyvtar.DatabaseConnection;
import com.example.konyvtar.BasicData;
import com.example.konyvtar.input.Input;
import com.example.konyvtar.input.NumberInput;
import com.example.konyvtar.input.ValidationResult;
import com.example.konyvtar.model.Month;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.util.*;


public class AddRentController implements Initializable, BasicData {
    @FXML
    ComboBox kolcsonzesFelveteleEv,kolcsonzesFelveteleHonap,kolcsonzesFelveteleNap;
    @FXML
    TextField serial;
    @FXML
    TextField membershipId;
    @FXML
    Label errorMessage;

    TextInput serialInput;
    NumberInput membershipIdInput;
    private List<Input> inputs;
    private Connection conn;

    public AddRentController() {
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        Calendar datum = Calendar.getInstance();
        int ev = datum.get(Calendar.YEAR);
        ObservableList<Integer> evek = FXCollections.observableArrayList();

        serialInput=new TextInput(serial);
        serialInput.setOnValidationFail(validationResult -> {
            errorMessage.setText((validationResult)+" a sorozat szám!");
        });
        membershipIdInput=new NumberInput(membershipId,MEMERSHIPIDLENGTH,MEMERSHIPIDLENGTH);
        membershipIdInput.setOnValidationFail(validationResult -> {
            errorMessage.setText((validationResult)+" a tagId!");
        });




        this.conn = DatabaseConnection.getConnection();

        for (int i = ev; i < ev + 2; ++i) {
            evek.add(i);
        }

        this.kolcsonzesFelveteleEv.setItems(evek);
        ObservableList<Integer> napok = FXCollections.observableArrayList();

        for (int i = 1; i <= 31; ++i) {
            napok.add(i);
        }

        this.kolcsonzesFelveteleNap.setItems(napok);
        ObservableList<Month> months = FXCollections.observableArrayList();

        for (int i = 1; i < MONTHS.length; ++i) {
            Month h = new Month(i, MONTHS[i]);
            months.add(h);
        }

        this.kolcsonzesFelveteleHonap.setItems(months);
    }

    public boolean checkNumberInput(String number, int length) {
        String regex = "\\d{" + length + "}";
        return number.matches(regex);
    }

    public boolean checkExistanceInDB(String table, String attribute, String element) {
        String sql = "SELECT * FROM " + table + " where " + attribute + "=?";
        boolean found = false;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, element);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return found;
    }

    public void loan() {
        String serial = serialInput.getValue();
        String membershipId = membershipIdInput.getValue();

        boolean okSerial = serial.matches("^[A-Z]{4}-\\d{5}$");
        boolean okMembershipId = checkNumberInput(membershipIdInput.getValue(), MEMERSHIPIDLENGTH);
        boolean okDate = (kolcsonzesFelveteleEv.getValue() != null) && (kolcsonzesFelveteleHonap.getValue() != null) && (kolcsonzesFelveteleNap.getValue() != null);
        if (!okSerial) {
            this.errorMessage.setText("Nem megfelelő leltári szám formátum!");
        } else {
            okSerial = checkExistanceInDB("leltar", "leltar_leltariszam", serial);
            if (!okSerial) {
                this.errorMessage.setText("A megadott sorozat számmal nem található könyv!");
            }
            if (!okMembershipId) {
                this.errorMessage.setText("Nem megfelelő tagsági azonosító formátum!");
            } else {
                okMembershipId = checkExistanceInDB("tag", "tag_id", membershipId);

                if (!okMembershipId) {
                    this.errorMessage.setText("A megadott tagsági azonosítóhoz nem tartozik felhasználó!");
                } else if (!okDate) {
                    this.errorMessage.setText("Nincs kiválasztva dátum!");
                }
            }
        }


        if (okSerial && okMembershipId && okDate) {
            errorMessage.setText("");
            String date = kolcsonzesFelveteleEv.getValue().toString() + "-" + (kolcsonzesFelveteleHonap.getSelectionModel().getSelectedIndex() + 1) + "-" + kolcsonzesFelveteleNap.getValue();
            PreparedStatement pstmt = null;
            try {
                pstmt = conn.prepareStatement("INSERT INTO kolcsonzes (leltar_leltariszam, tag_id, kolcsonzes_datum, kolcsonzes_hatar) VALUES (?,?,?,?)");
                pstmt.setString(1, serial);
                pstmt.setString(2, membershipId);
                Instant now = Instant.now();
                Timestamp timestamp = Timestamp.from(now);
                pstmt.setTimestamp(3, timestamp);
                pstmt.setString(4, date);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sikeres hozzáadás!");
                    alert.setHeaderText(null);
                    alert.setContentText("Az " + membershipId + " azonosítójú taghoz az " + serial + " azonosítójú könyv sikeresen hozzáadva.");
                    alert.showAndWait();

                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
}

