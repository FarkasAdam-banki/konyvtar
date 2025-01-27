package com.example.konyvtar.input;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ConnectedTextInput extends Input{

    private List<TextInput> inputs;
    private String separator;

    public ConnectedTextInput(List<TextInput> inputs) {
        setInputs(inputs);
    }

    public ConnectedTextInput() {
        this(new ArrayList<>());
    }

    public void addInput(TextInput input) {
        inputs.add(input);
    }

    public String getValue() {
        if (inputs.isEmpty()) throw new NoSuchElementException("A lista üres!");
        StringBuilder value = new StringBuilder();
        for (TextInput i : inputs) {
            value.append(i.getValue());
        }
        return value.toString();
    }

    @Override
    protected ValidationResult validate() {
        return null;
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
}
