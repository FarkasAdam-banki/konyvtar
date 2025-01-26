package com.example.konyvtar.input;

import java.util.function.Consumer;

public abstract class Input {

    private Consumer<ValidationResult> onValidationFail = null;

    public final boolean isValid() {
        return validate() == ValidationResult.VALID;
    }

    public final boolean isValidOrFail() {
        if (isValid()) return true;
        if(onValidationFail != null)
            onValidationFail.accept(validate());
        return false;
    }

    protected abstract ValidationResult validate();

    public Consumer<ValidationResult> getOnValidationFail() {
        return onValidationFail;
    }

    public void setOnValidationFail(Consumer<ValidationResult> onValidationFail) {
        this.onValidationFail = onValidationFail;
    }
}
