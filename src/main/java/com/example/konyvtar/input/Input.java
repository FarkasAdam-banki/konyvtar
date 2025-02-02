package com.example.konyvtar.input;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Input {

    private Consumer<ValidationResult> onValidationFail = null;
    private Predicate<Input> customValidator = null;

    public final boolean isValid() {
        if (customValidator != null) {
            return validate() == ValidationResult.VALID && customValidator.test(this);
        }
        return validate() == ValidationResult.VALID;
    }

    public final boolean isValidOrFail() {
        if (isValid())
            return true;
        if(onValidationFail != null){
            if (validate() != ValidationResult.VALID){
                onValidationFail.accept(validate());
            } else {
                onValidationFail.accept(ValidationResult.CUSTOM_VALIDATION_FAIL);
            }
        }
        return false;
    }

    protected abstract ValidationResult validate();

    public Consumer<ValidationResult> getOnValidationFail() {
        return onValidationFail;
    }

    public void setOnValidationFail(Consumer<ValidationResult> onValidationFail) {
        this.onValidationFail = onValidationFail;
    }

    public Predicate<Input> getCustomValidator() {
        return customValidator;
    }

    public void setCustomValidator(Predicate<Input> customValidator) {
        this.customValidator = customValidator;
    }
}
