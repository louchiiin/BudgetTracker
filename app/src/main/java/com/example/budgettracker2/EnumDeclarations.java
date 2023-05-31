package com.example.budgettracker2;

public enum EnumDeclarations {
    INCOME(1),
    EXPENSE(2),
    TRANSFER(3);

    private final int value;

    EnumDeclarations(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
