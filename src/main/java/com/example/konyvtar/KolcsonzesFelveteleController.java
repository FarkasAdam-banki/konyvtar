package com.example.konyvtar;

import com.example.konyvtar.input.Input;
import com.example.konyvtar.input.NumberInput;
import com.example.konyvtar.input.ValidationResult;
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


public class KolcsonzesFelveteleController implements Initializable, basicDatas {
    @FXML
    ComboBox kolcsonzesFelveteleEv,kolcsonzesFelveteleHonap,kolcsonzesFelveteleNap;
    @FXML
    TextField isbn;
    @FXML
    TextField membershipId;
    @FXML
    Label errorMessage;

    NumberInput isbnInput,membershipIdInput;
    private List<Input> inputs;
    private Connection conn;

    public KolcsonzesFelveteleController() {
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        Calendar datum = Calendar.getInstance();
        int ev = datum.get(1);
        ObservableList<Integer> evek = FXCollections.observableArrayList();

        isbnInput=new NumberInput(isbn,ISBNLENGTH,ISBNLENGTH);
        isbnInput.setOnValidationFail(validationResult -> {
            errorMessage.setText(getErrorMessageNumberInput(validationResult)+"az isbn szám!");
        });
        membershipIdInput=new NumberInput(membershipId,MEMERSHIPIDLENGTH,MEMERSHIPIDLENGTH);
        isbnInput.setOnValidationFail(validationResult -> {
            errorMessage.setText(getErrorMessageNumberInput(validationResult)+"a tagId!");
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
            pstmt.setLong(1, Long.parseLong(element));
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
        inputs = Arrays.asList( isbnInput, membershipIdInput);
        onSubmit();
        String isbn = isbnInput.getValue();
        String membershipId = membershipIdInput.getValue();

        boolean okIsbn = checkNumberInput(isbnInput.getValue(), ISBNLENGTH);
        boolean okMembershipId = checkNumberInput(membershipIdInput.getValue(), MEMERSHIPIDLENGTH);
        boolean okDate = (kolcsonzesFelveteleEv.getValue() != null) && (kolcsonzesFelveteleHonap.getValue() != null) && (kolcsonzesFelveteleNap.getValue() != null);
        if (!okIsbn) {
            this.errorMessage.setText("Nem megfelelő ISBN!");
        } else {
            okIsbn = checkExistanceInDB("konyv", "konyv_ISBN", isbn);
            if (!okIsbn) {
                this.errorMessage.setText("A megadott ISBN-el nem található könyv!");
            }
            if (!okMembershipId) {
                this.errorMessage.setText("Nem megfelelő tagsági azonosító!");
            } else {
                okMembershipId = checkExistanceInDB("tag", "tag_id", membershipId);

                if (!okMembershipId) {
                    this.errorMessage.setText("A megadott tagsági azonosítóhoz nem tartozik felhasználó!");
                } else if (!okDate) {
                    this.errorMessage.setText("Nincs kiválasztva dátum!");
                }
            }
        }


        if (okIsbn && okMembershipId && okDate) {
            errorMessage.setText("");
            String date = kolcsonzesFelveteleEv.getValue().toString() + "-" + (kolcsonzesFelveteleHonap.getSelectionModel().getSelectedIndex() + 1) + "-" + kolcsonzesFelveteleNap.getValue();
            PreparedStatement pstmt = null;
            try {
                pstmt = conn.prepareStatement("INSERT INTO kolcsonzes (konyv_ISBN, tag_id, kolcsonzes_datum, kolcsonzes_hatar) VALUES (?,?,?,?)");
                pstmt.setString(1, isbn);
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
                    alert.setContentText("Az " + membershipId + " azonosítójú taghoz az " + isbn + " azonosítójú könyv sikeresen hozzáadva.");
                    alert.showAndWait();

                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
    private String getErrorMessageNumberInput(ValidationResult result) {
        return switch (result) {
            case EMPTY -> "Nincs megadva ";
            case TOO_LONG -> "Túl sok számjegyből áll ";
            case REGEX_FAIL -> "Nem egy szám ";
            case NUMBER_TOO_SMALL -> "Túl kicsi értékű ";
            case NUMBER_TOO_LARGE -> "Túl nagy értékű ";
            default -> throw new IllegalStateException("Unexpected value: " + result);
        };
    }

    public void onSubmit() {
        int i = 0;
        while (i < inputs.size() && inputs.get(i).isValidOrFail()){
            i++;
        }
        if (i == inputs.size()) {
            errorMessage.setText("");
            System.out.println("Valid!");
        }

    }
}

