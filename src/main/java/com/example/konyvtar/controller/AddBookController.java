package com.example.konyvtar.controller;

import com.example.konyvtar.DatabaseConnection;
import com.example.konyvtar.input.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AddBookController implements Initializable {
    @FXML
    private Label errorMessage;
    @FXML
    private ComboBox<String> kolcsonozheto;
    @FXML
    private TextField cim, szerzo, kiadas, mufaj, isbn;

    private Select<String> kolcsonozhetoSelect;
    private TextInput cimInput, szerzoInput, mufajInput;
    private NumberInput kiadasInput, isbnInput;

    private List<Input> inputs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<String> options = Arrays.asList("Válasszon!", "Igen", "Nem");
        kolcsonozhetoSelect = new Select<>(kolcsonozheto, options);
        kolcsonozhetoSelect.setOnValidationFail(_ -> errorMessage.setText("Nincs kiválasztva, hogy kölcsönözhető-e!"));

        cimInput = new TextInput(cim, 50);
        cimInput.setOnValidationFail(validationResult -> {
            errorMessage.setText(getErrorMessageTextInput(validationResult)+"a könyv címe!");
        });
        szerzoInput = new TextInput(szerzo, 50);
        szerzoInput.setOnValidationFail(validationResult -> {
            errorMessage.setText(getErrorMessageTextInput(validationResult)+"a szerző!");
        });
        kiadasInput = new NumberInput(kiadas, 4);
        kiadasInput.setMinValue(0);
        kiadasInput.setMaxValue(2025);
        kiadasInput.setOnValidationFail(validationResult -> {
            errorMessage.setText(getErrorMessageNumberInput(validationResult)+"a kiadás éve!");
        });
        mufajInput = new TextInput(mufaj, 20);
        mufajInput.setOnValidationFail(validationResult -> {
            errorMessage.setText(getErrorMessageTextInput(validationResult)+"a műfaj!");
        });
        isbnInput = new NumberInput(isbn, 13);
        isbnInput.setOnValidationFail(validationResult -> {
            errorMessage.setText(getErrorMessageNumberInput(validationResult)+"az ISBN-kód!");
        });

        inputs = Arrays.asList(cimInput, szerzoInput, kiadasInput, mufajInput, isbnInput, kolcsonozhetoSelect);
    }

    public void onSubmit() {
        int i = 0;
        while (i < inputs.size() && inputs.get(i).isValidOrFail()){
            i++;
        }
        if (i == inputs.size()) {
            errorMessage.setText("");
            PreparedStatement pstmnt = DatabaseConnection.getPreparedStatement(
        "INSERT INTO `konyv` (`konyv_ISBN`, `konyv_cim`, `konyv_szerzo`, `konyv_kiadas`, `konyv_mufaj`, `konyv_statusz`) VALUES (?, ?, ?, ?, ?, ?)"
            );
            try {
                pstmnt.setLong(1, isbnInput.getNumber());
                pstmnt.setString(2, cimInput.getValue());
                pstmnt.setString(3, szerzoInput.getValue());
                pstmnt.setInt(4, (int)kiadasInput.getNumber());
                pstmnt.setString(5, mufajInput.getValue());
                pstmnt.setInt(6, 1 - kolcsonozhetoSelect.getSelectedIndex());
                DatabaseConnection.executeUpdate(pstmnt);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Sikeres felvétel");
                alert.setContentText("A \""+ cimInput.getValue() +"\" című könyv sikeresen felvéve!");
                alert.show();
            }catch (SQLException sqle) {
                System.err.println(sqle.getMessage());
            }
        }

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

    private String getErrorMessageNumberInput(ValidationResult result) {
        return switch (result) {
            case EMPTY -> "Nincs megadva ";
            case TOO_SHORT -> "Túl kevés számjegyből áll ";
            case TOO_LONG -> "Túl sok számjegyből áll ";
            case REGEX_FAIL -> "Nem egy szám ";
            case NUMBER_TOO_SMALL -> "Túl kicsi értékű ";
            case NUMBER_TOO_LARGE -> "Túl nagy értékű ";
            default -> throw new IllegalStateException("Unexpected value: " + result);
        };
    }

}
