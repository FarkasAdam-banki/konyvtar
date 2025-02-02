package com.example.konyvtar.input;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.util.List;

public class Select<T> extends Input {
    private ComboBox<T> comboBox;
    private boolean includesPrompt;

    public Select(ComboBox<T> comboBox, boolean includesPrompt, List<T> options) {
        this(comboBox, includesPrompt);
        setOptions(options);
    }

    public Select(ComboBox<T> comboBox, boolean includesPrompt) {
        setComboBox(comboBox);
        setIncludesPrompt(includesPrompt);
    }

    public Select(ComboBox<T> comboBox, List<T> options) {
        this(comboBox, true, options);
    }

    public Select(ComboBox<T> comboBox) {
        this(comboBox, true);
    }

    @Override
    protected ValidationResult validate() {
        if (getSelectedIndex() == -1 || (includesPrompt && getSelectedIndex() == 0))
            return ValidationResult.NO_SELECTION;

        return ValidationResult.VALID;
    }

    @Override
    public void reset() {
        select(0);
    }

    public int getSelectedIndex() {
        return comboBox.getSelectionModel().getSelectedIndex();
    }

    public T getSelectedItem() {
        return comboBox.getSelectionModel().getSelectedItem();
    }

    public void select(int index) {
        comboBox.getSelectionModel().select(index);
    }

    public void setOptions(List<T> options) {
        ObservableList<T> observableList = FXCollections.observableArrayList(options);
        comboBox.setItems(observableList);
        if (includesPrompt) {
            select(0);
        }
    }

    public void addOption(T option) {
        ObservableList<T> items = comboBox.getItems();
        items.add(option);
        if (items.size() == 1 && includesPrompt) {
            select(0);
        }
    }


    public ComboBox<T> getComboBox() {
        return comboBox;
    }

    public void setComboBox(ComboBox<T> comboBox) {
        this.comboBox = comboBox;
    }

    public boolean isIncludesPrompt() {
        return includesPrompt;
    }

    public void setIncludesPrompt(boolean includesPrompt) {
        this.includesPrompt = includesPrompt;
    }

}
