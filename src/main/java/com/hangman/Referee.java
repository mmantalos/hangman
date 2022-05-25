package com.hangman;

public class Referee {
    private Integer score = 0, attempts =  0, failed_attempts = 0;

    public int getScore() {
        return score;
    }

    public double getSuccessPercent() {
        return ((int) (10000.0 * (attempts - failed_attempts) / attempts)) / 100.0;
    }

    public void addAttempt(int attempt_score) {
        attempts++;
        if (attempt_score < 0)
            failed_attempts++;

        score += attempt_score;
        if (score < 0)
            score = 0;
    }

    public int getFailed() {
        return failed_attempts;
    }

    public boolean lost() {
        return failed_attempts == 6;
    }
}
