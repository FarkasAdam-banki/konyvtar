package com.example.konyvtar.input;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ConnectedTextInput extends Input{

    private List<TextInput> inputs;
    private String separator;
    private String regex;
    private int maxLength;

    public ConnectedTextInput(List<TextInput> inputs, String separator, int maxLength) {
        setInputs(inputs);
        setSeparator(separator);
        setMaxLength(maxLength);
    }

    public ConnectedTextInput(List<TextInput> inputs,String separator) {
        this(inputs, separator, -1);
    }

    public ConnectedTextInput(String separator) {
        this(new ArrayList<>(), separator);
    }

    public ConnectedTextInput() {
        this(" ");
    }

    public void addInput(TextInput input) {
        inputs.add(input);
    }

    public String getValue() {
        if (inputs.isEmpty()) throw new NoSuchElementException("A lista üres!");
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < inputs.size(); i++) {
            TextInput input = inputs.get(i);
            value.append(input.getValue());
            if (i+1 != inputs.size()){
                value.append(separator);
            }
        }
        return value.toString().trim();
    }

    @Override
    protected ValidationResult validate() {
        if (inputs.isEmpty()) return ValidationResult.GROUP_EMPTY;
        int i = 0;
        while (i < inputs.size() && inputs.get(i).isValid()) {
            i++;
        }
        if (i != inputs.size()) {
            return inputs.get(i).validate();
        }
        if (getValue().length() > maxLength){
            return ValidationResult.GROUP_TOO_LONG;
        }
        if (!getValue().matches(regex)) {
            return ValidationResult.GROUP_REGEX_FAIL;
        }
        return ValidationResult.VALID;
    }

    public void setInputs(List<TextInput> inputs) {
        this.inputs = inputs;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
