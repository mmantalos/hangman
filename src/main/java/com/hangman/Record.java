package com.hangman;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Record {
    private SimpleIntegerProperty score;
    private SimpleStringProperty winner;

    Record(int s, String w) {
        score = new SimpleIntegerProperty(s);
        winner = new SimpleStringProperty(w);
    }

    public int getScore() {
        return score.get();
    }

    public String getWinner() {
        return winner.get();
    }
}
