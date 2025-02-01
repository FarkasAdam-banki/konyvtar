package com.example.konyvtar.controller;

import com.example.konyvtar.DatabaseConnection;
import com.example.konyvtar.MainApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private BorderPane bp;
    @FXML
    public void closeApplication() {
        Platform.exit();
    }

    public void showKolcsonzesFelvetele() {
        setFXMLContents("add-rent.fxml");
    }
    public void showKolcsonzesekListaja() {
        setFXMLContents("rent-list.fxml");
    }
    public void showKonyvFelvetel() {
        setFXMLContents("add-book.fxml");
    }
    public void showKonyvekKeresese() {
        setFXMLContents("find-book.fxml");
    }
    public void showTagRegisztralasa() {
        setFXMLContents("add_member.fxml");
    }
    public void showTagokListaja() {
        setFXMLContents("member_list.fxml");
    }

    public void setFXMLContents(String fileName) {
        Parent root;
        try {
            root = FXMLLoader.load(MainApplication.class.getResource("views/"+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bp.setCenter(root);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!DatabaseConnection.isAvailable()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba");
            alert.setHeaderText("Adatbázishiba");
            alert.setContentText("Az adatbázis nem elérhető!");
            alert.showAndWait();
            closeApplication();
        }
    }
}