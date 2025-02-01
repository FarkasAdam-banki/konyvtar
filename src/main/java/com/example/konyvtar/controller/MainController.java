package com.example.konyvtar.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {
    @FXML
    private BorderPane bp;
    public void showKolcsonzesFelvetele() {
        setFXMLContents("kolcsonzes-felvetele.fxml");
    }
    public void showKolcsonzesekListaja() {
        setFXMLContents("kolcsonzesek-listaja.fxml");
    }
    public void showKonyvFelvetel() {
        setFXMLContents("konyv-felvetel.fxml");
    }
    public void showKonyvekKeresese() {
        setFXMLContents("konyvek-keresese.fxml");
    }
    public void showTagRegisztralasa() {
        setFXMLContents("tag-regisztralasa.fxml");
    }
    public void showTagokListaja() {
        setFXMLContents("tagok-listaja.fxml");
    }

    public void setFXMLContents(String fileName) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("views/"+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bp.setCenter(root);
    }
}