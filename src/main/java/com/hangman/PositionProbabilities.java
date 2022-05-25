package com.hangman;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PositionProbabilities {
    private final SimpleIntegerProperty position;
    private final SimpleStringProperty probabilities;

    PositionProbabilities(int p, String prob) {
        position = new SimpleIntegerProperty(p);
        probabilities = new SimpleStringProperty(prob);
    }

    public int getPosition() {
        return position.get();
    }

    public String getProbabilities() {
        return probabilities.get();
    }
}
