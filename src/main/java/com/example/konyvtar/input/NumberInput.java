package com.example.konyvtar.input;

import javafx.scene.control.TextField;

public class NumberInput extends TextInput{

    private long minValue, maxValue;

    public NumberInput(TextField textField) {
        this(textField, false);
    }

    public NumberInput(TextField textField, boolean optional) {
        this(textField, optional, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public NumberInput(TextField textField, int minValue, int maxValue) {
        this(textField, false, minValue, maxValue);
    }

    public NumberInput(TextField textField, boolean optional, long minValue, long maxValue) {
        super(textField, optional, "^\\d+$", -1);
        setMinValue(minValue);
        setMaxValue(maxValue);
    }

    public NumberInput(TextField textField, int digitNumber) {
        this(textField, false, digitNumber);
    }

    public NumberInput(TextField textField, boolean optional, int digitNumber) {
        super(textField, optional, "^\\d+$", digitNumber);
        setMinValue(Long.MIN_VALUE);
        setMaxValue(Long.MAX_VALUE);
    }

    @Override
    protected ValidationResult validate() {
        ValidationResult validationResult = super.validate();
        if (validationResult != ValidationResult.VALID)
            return validationResult;
        long num = getNumber();
        if (num < minValue)
            return ValidationResult.NUMBER_TOO_SMALL;
        if (num > maxValue)
            return ValidationResult.NUMBER_TOO_LARGE;
        return ValidationResult.VALID;
    }

    public long getNumber() {
        return Long.parseLong(getValue());
    }

    public void setNumber(long number) {
        setValue(String.valueOf(number));
    }

    public long getMinValue() {
        return minValue;
    }

    public void setMinValue(long minValue) {
        this.minValue = minValue;
    }

    public long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(long maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public void setRegex(String regex) {
        if (!regex.equals("^\\d+$"))
            throw new UnsupportedOperationException("Cannot set regex for numbers!");
        else {
            super.setRegex(regex);
        }
    }
}
