package com.myptai.condition.domain;

public enum EnergyLevel {
    GOOD("좋음"),
    NORMAL("보통"),
    TIRED("피곤함"),
    EXHAUSTED("매우 피곤함");

    private final String label;

    EnergyLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
