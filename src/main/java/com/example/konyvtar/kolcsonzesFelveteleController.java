package com.example.konyvtar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.util.Calendar;
import java.util.ResourceBundle;


public class kolcsonzesFelveteleController implements Initializable, basicDatas {
    @FXML
    ComboBox kolcsonzesFelveteleEv;
    @FXML
    ComboBox kolcsonzesFelveteleHonap;
    @FXML
    ComboBox kolcsonzesFelveteleNap;
    @FXML
    TextField isbn;
    @FXML
    TextField membershipId;
    @FXML
    Label errorMessage;
    private Connection conn;
    public kolcsonzesFelveteleController(){
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        Calendar datum = Calendar.getInstance();
        int ev = datum.get(1);
        ObservableList<Integer> evek = FXCollections.observableArrayList();
        this.conn = DatabaseConnection.getConnection();
        for(int i = ev; i < ev+2; ++i) {
            evek.add(i);
        }

        this.kolcsonzesFelveteleEv.setItems(evek);
        ObservableList<Integer> napok = FXCollections.observableArrayList();

        for(int i = 1; i <= 31; ++i) {
            napok.add(i);
        }

        this.kolcsonzesFelveteleNap.setItems(napok);
        ObservableList<Month> months = FXCollections.observableArrayList();

        for(int i = 1; i < MONTHS.length; ++i) {
            Month h = new Month(i, MONTHS[i]);
            months.add(h);
        }

        this.kolcsonzesFelveteleHonap.setItems(months);
    }
    public boolean checkNumberInput(String number,int length){
        String regex = "\\d{"+length+"}";
        return number.matches(regex);
    }

    public void loan() {
        String isbn = this.isbn.getText();
        String membershipId = this.membershipId.getText();

        //isbn létezés ellenőrzése
        boolean okIsbn=checkNumberInput(isbn,ISBNLENGTH);
        boolean okMembershipId=checkNumberInput(membershipId,MEMERSHIPIDLENGTH);
        boolean okDate=kolcsonzesFelveteleEv.getValue()!=null||kolcsonzesFelveteleHonap.getValue()!=null||kolcsonzesFelveteleHonap.getValue()!=null;
        if(!okIsbn){
            this.errorMessage.setText("Nem megfelelő ISBN!");
        }
        else if(!okMembershipId){
            this.errorMessage.setText("Nem megfelelő tagsági azonosító!");
        } else if (!okDate) {
            this.errorMessage.setText("Nincs kiválasztva dátum!");
        }


        if (okIsbn && okMembershipId && okDate) {
            String date=kolcsonzesFelveteleEv.getValue().toString()+"-" + (kolcsonzesFelveteleHonap.getSelectionModel().getSelectedIndex()+1) +"-"+ kolcsonzesFelveteleNap.getValue();
            PreparedStatement stmt = null;
            try {
                stmt = conn.prepareStatement("INSERT INTO kolcsonzes (konyv_ISBN, tag_id, kolcsonzes_datum, kolcsonzes_hatar) VALUES (?,?,?,?)");
                stmt.setString(1,isbn);
                stmt.setString(2,membershipId);
                Instant now = Instant.now();
                Timestamp timestamp = Timestamp.from(now);
                System.out.println(timestamp);
                System.out.println(date);
                stmt.setTimestamp(3, timestamp);
                stmt.setString(4,date);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Loan inserted successfully.");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
    }

