package com.example.konyvtar.controller;

import com.example.konyvtar.DatabaseConnection;
import com.example.konyvtar.BasicData;
import com.example.konyvtar.input.*;
import com.example.konyvtar.model.Month;
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
import java.util.function.Consumer;


public class AddRentController implements Initializable, BasicData {
    @FXML
    ComboBox<Integer> rentYear, rentDay;
    @FXML
    ComboBox<Month> rentMonth;
    @FXML
    TextField serial;
    @FXML
    TextField membershipId;
    @FXML
    Label errorMessage;

    Select<Integer> yearSelect, daySelect;
    Select<Month> monthSelect;
    TextInput serialInput;
    NumberInput membershipIdInput;
    private List<Input> inputs;

    public AddRentController() {
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        Calendar datum = Calendar.getInstance();
        int ev = datum.get(Calendar.YEAR);

        serialInput=new TextInput(serial);
        serialInput.setOnValidationFail(validationResult -> {
            String err = switch (validationResult) {
                case EMPTY -> "Nincs megadva a leltári szám!";
                case CUSTOM_VALIDATION_FAIL -> "A megadott sorozat számmal nem található könyv!";
                default -> "Nem megfelelő leltári szám formátum!";
            };
            errorMessage.setText(err);
        });
        serialInput.setRegex("^[A-Z]{4}-\\d{5}$");
        serialInput.setCustomValidator(input -> {
            TextInput textInput = (TextInput) input;
            return checkExistanceInDB("leltar", "leltar_leltariszam", textInput.getValue());
        });
        membershipIdInput=new NumberInput(membershipId, MEMERSHIPIDLENGTH);
        membershipIdInput.setMinValue(MEMERSHIPIDLENGTH);
        membershipIdInput.setOnValidationFail(validationResult -> {
            String err = switch (validationResult) {
                case EMPTY -> "Nincs megadva a tagsági azonosító!";
                case CUSTOM_VALIDATION_FAIL -> "A megadott tagsági azonosítóhoz nem tartozik felhasználó!";
                default -> "NNem megfelelő tagsági azonosító formátum!";
            };
            errorMessage.setText(err);
        });
        membershipIdInput.setCustomValidator(input -> {
            TextInput textInput = (TextInput) input;
            return checkExistanceInDB("tag", "tag_id", textInput.getValue());
        });
        Consumer<ValidationResult> errorDate = _ -> errorMessage.setText("Nincs megadva dátum!");
        yearSelect = new Select<>(rentYear, false);
        yearSelect.setOnValidationFail(errorDate);
        monthSelect = new Select<>(rentMonth, false);
        monthSelect.setOnValidationFail(errorDate);
        daySelect = new Select<>(rentDay, false);
        daySelect.setOnValidationFail(result -> {
            errorMessage.setText(result == ValidationResult.CUSTOM_VALIDATION_FAIL? "Érvénytelen dátum!":"Nincs megadva dátum!");
        });
        daySelect.setCustomValidator(_ ->
            datum.get(Calendar.DAY_OF_MONTH) < daySelect.getSelectedIndex() + 1 && datum.get(Calendar.MONTH) == monthSelect.getSelectedIndex() ||
                    datum.get(Calendar.MONTH) < monthSelect.getSelectedIndex() && ev == yearSelect.getSelectedItem() ||
                    yearSelect.getSelectedItem() > ev);

        for (int i = ev; i < ev + 2; ++i) {
            yearSelect.addOption(i);
        }
        for (int i = 1; i <= 31; ++i) {
            daySelect.addOption(i);
        }
        for (int i = 1; i < MONTHS.length; ++i) {
            Month h = new Month(i, MONTHS[i]);
            monthSelect.addOption(h);
        }
        inputs = List.of(serialInput, membershipIdInput, yearSelect, monthSelect, daySelect);

    }

    public boolean checkExistanceInDB(String table, String attribute, String element) {
        String sql = "SELECT * FROM " + table + " where " + attribute + "=?";
        boolean found = false;
        try {
            PreparedStatement pstmt = DatabaseConnection.getPreparedStatement(sql);
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
        int i = 0;
        while (i < inputs.size() && inputs.get(i).isValidOrFail()){
            i++;
        }

        if (i == inputs.size()) {
            errorMessage.setText("");
            String date = yearSelect.getSelectedItem() + "-" + (monthSelect.getSelectedIndex()+1) + "-" + daySelect.getSelectedItem();
            PreparedStatement pstmt;
            boolean canBorrow;
            try {
                pstmt = DatabaseConnection.getPreparedStatement("SELECT `kolcsonzes_visszaE` FROM `kolcsonzes` WHERE `leltar_leltariszam` = ? ORDER BY `kolcsonzes_id` DESC LIMIT 1;");
                pstmt.setString(1, serialInput.getValue());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    canBorrow = rs.getBoolean(1);
                }else {
                    canBorrow = true;
                }
            }catch (SQLException sqle) {
                throw new RuntimeException(sqle);
            }
            if (canBorrow) {
                try {
                    pstmt = DatabaseConnection.getPreparedStatement("INSERT INTO kolcsonzes (leltar_leltariszam, tag_id, kolcsonzes_datum, kolcsonzes_hatar, kolcsonzes_visszaE) VALUES (?,?,?,?,0)");
                    pstmt.setString(1, serialInput.getValue());
                    pstmt.setString(2, membershipIdInput.getValue());
                    Instant now = Instant.now();
                    Timestamp timestamp = Timestamp.from(now);
                    pstmt.setTimestamp(3, timestamp);
                    pstmt.setString(4, date);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Sikeres hozzáadás!");
                        alert.setHeaderText(null);
                        alert.setContentText("Az " + membershipIdInput.getValue() + " azonosítójú taghoz az " + serialInput.getValue() + " azonosítójú könyv sikeresen hozzáadva.");
                        alert.show();

                        for (Input j : inputs) {
                            j.reset();
                        }
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else {
                errorMessage.setText("Ez a könyv nem kikölcsönözhető!");
            }
        }

    }
}

