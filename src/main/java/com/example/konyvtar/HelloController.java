package com.example.konyvtar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class HelloController {
    @FXML
    private BorderPane bp;
    public void showKolcsonzesFelvetele() {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("kolcsonzes-felvetele.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bp.setCenter(root);
    }
}