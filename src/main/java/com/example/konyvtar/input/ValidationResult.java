package com.example.konyvtar.input;

public enum ValidationResult {
    VALID,
    EMPTY,
    REGEX_FAIL,
    TOO_LONG,
    NO_SELECTION,
    NUMBER_TOO_SMALL,
    NUMBER_TOO_LARGE,
    GROUP_EMPTY,
    GROUP_TOO_LONG,
    GROUP_REGEX_FAIL
}
