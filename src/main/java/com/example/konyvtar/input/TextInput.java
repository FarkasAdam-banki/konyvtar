package com.example.konyvtar.input;

import javafx.scene.control.TextField;

public class TextInput extends Input {
    private TextField textField;
    private boolean optional;
    private String regex;
    private int maxLength;

    public TextInput(TextField textField, boolean optional, String regex, int maxLength) {
        setTextField(textField);
        setOptional(optional);
        setRegex(regex);
        setMaxLength(maxLength);
    }

    public TextInput(TextField textField, String regex, int maxLength) {
        this(textField, false, regex, maxLength);
    }

    public TextInput(TextField textField, boolean optional) {
        this(textField, optional, null, -1);
    }

    public TextInput(TextField textField, int maxLength) {
        this(textField, false, null, maxLength);
    }

    public TextInput(TextField textField) {
        this(textField, false, null, -1);
    }

    @Override
    protected ValidationResult validate() {
        if (maxLength > 0 && getValueLength() > maxLength)
            return ValidationResult.TOO_LONG;

        if (!optional && getValue().trim().isEmpty())
            return ValidationResult.EMPTY;

        if (regex != null && !getValue().matches(regex))
            return ValidationResult.REGEX_FAIL;
        if (!isString(getValue())) {
            return ValidationResult.NOT_STRING;
        }
        return ValidationResult.VALID;
    }

    public boolean isString(String data) {
        if (!Character.isUpperCase(data.charAt(0))) {
            return false;
        }
        for (int i = 1; i < data.length(); i++) {
            if (!Character.isLowerCase(data.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public void clearText() {
        setValue("");
    }

    public String getValue() {
        return textField.getText();
    }

    public void setValue(String text) {
        textField.setText(text);
    }

    public int getValueLength() {
        return getValue().length();
    }

    public TextField getTextField() {
        return textField;
    }

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
