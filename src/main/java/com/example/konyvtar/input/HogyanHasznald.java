package com.example.konyvtar.input;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;

public class HogyanHasznald {
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TextField textField;
    @FXML
    private TextField textFieldNumber;
    
    public void hogyanHasznald() {
        
        // Input ősosztályú objektumok
        // Select objektum vagyis a ComboBox leegyszerűsítése
        // szimpla konstruktor
        Select<String> select = new Select<>(comboBox);
        // az első index (0.) is tartalmaz egy kiválasztható opciót (alapból true) 
        Select<String> select1 = new Select<>(comboBox, false);
        
        // opciók hozzáadása
        select.addOption("Válasszon!"); // Ha includesPrompt = true
        select.addOption("1. opció");
        select.addOption("2. opció");
        // vagy
        List<String> options = Arrays.asList("1. opció", "2. opció");
        select1.setOptions(options);
        
        // kiválaszott opció lekérdezése
        select.getSelectedIndex();
        select.getSelectedItem();
        
        // opció manuális kiválasztása
        select.select(1);
        
        // TextInput objektum
        // szimpla konstruktor
        TextInput textInput = new TextInput(textField);
        // összes érték megadása
        // optional = nem kötelező a mezőt kitölteni (alapból false)
        // regex = validáló regex string (alapból null)
        // maxLength = maximum karakterszám (alapból -1 vagyis végtelen)
        TextInput textInput1 = new TextInput(textField, true, "", 3);
        // alternatív konstruktorok
        TextInput textInput2 = new TextInput(textField, true);
        TextInput textInput3 = new TextInput(textField, "", 3);
        TextInput textInput4 = new TextInput(textField, 3);
        
        // mindenhez van setter
        textInput2.setOptional(false);
        textInput3.setMaxLength(10);

        textInput.getValue();

        textInput.setValue("asd");

        textInput.getValueLength();
        
        // szöveg kitörlése
        textInput.clearText();
        
        // NumberInput objektum (őse a TextInput)
        // szimpla konstruktor
        NumberInput numberInput = new NumberInput(textFieldNumber);
        // optional = nem kötelező a mezőt kitölteni (alapból false)
        // minValue = minimum érték (alapból Long.MIN_VALUE)
        // maxValue = maximum érték (alapból Long.MAX_VALUE)
        NumberInput numberInput1 = new NumberInput(textFieldNumber, true, 10, 100);
        // optional = nem kötelező a mezőt kitölteni (alapból false)
        // digitNumber = maximum számjegyszám (alapból -1 vagyis végtelen)
        NumberInput numberInput2 = new NumberInput(textFieldNumber, false, 3);
        // !!! Nem lehet egyszerre beállítani min-max értéket és maximum számjegyszámot egy konstruktorban (settereket kell használni) !!!
        // alternatív konstruktorok
        NumberInput numberInput3 = new NumberInput(textFieldNumber, true);
        NumberInput numberInput4 = new NumberInput(textFieldNumber, 1, 10);
        NumberInput numberInput5 = new NumberInput(textFieldNumber, 10);

        // longként adja vissza a számokat
        long a = numberInput.getNumber();
        // stringként a getValue metódussal
        numberInput.getValue();



        // Közös metódusok:
        // isValid - általános validáció
        if (textInput.isValid()) {
            // Valid
        }

        // validate - visszaad egy ValidationResult-ot
        ValidationResult validationResult = select.validate();

        // switch szerkezettel így visszakereshető a hiba oka
        switch (validationResult) {
            case VALID -> {
                // helyesen lett a kitöltve minden
            }
            case EMPTY -> {
                // TextInputnál vagy NumberInputnál ha üres a mező
                // (csak akkor ha optional = false)
            }
            case REGEX_FAIL -> {
                // TextInputnál ha nem fut le a regex
                // NumberInputnál ha nem egy szám van beírva
            }
            case TOO_LONG -> {
                // TextInputnál ha a karakter limitet meghaladja a mező
                // NumberInputnál ha a számjegy limitet meghaladja a mező
            }
            case NO_SELECTION -> {
                // Selectnél ha a 0. index van kiválasztva
                // (csak akkor ha includesPrompt = true)
                // Ez az egyetlen előforduló validációs hiba a Select objektumnál
            }
            case NUMBER_TOO_SMALL -> {
                // NumberInputnál ha túl kicsi az érték
            }
            case NUMBER_TOO_LARGE -> {
                // NumberInputnál ha túl nagy az érték
            }
        }

        // isValidOrFail - validáció mellett lefuttatja az onValidationFail-t ha sikertelen a validáció
        if (textInput3.isValidOrFail()) {
            // Vagy ez fut le, vagy az onValidationFail
        }

        // onValidationFail beállítása lambdával (nyíl függvény)
        textInput.setOnValidationFail(result -> {
            // A result a validation() metódus értéke
            System.out.println("Sikertelen validáció");
            if(result == ValidationResult.TOO_LONG) {
                // if vagy switch ugyanúgy megvizsgálja az okát
            }
        });

        // validáció megvalósítása a AddBookController osztályban található
    }
}
