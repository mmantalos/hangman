package com.hangman;

import java.util.Collections;

import java.util.List;
import java.util.ArrayList;

public class SecretWord {
    private final String secret_word;
    private char[] current_guess;
    private List<String> possible_words;

    private Double[][] probabilities;

    SecretWord(String word, List<String> word_list) {
        secret_word = word;
        current_guess = "_".repeat(word.length()).toCharArray();

        possible_words = new ArrayList<>(word_list);
        possible_words.removeIf(s -> s.length() != secret_word.length());

        probabilities = new Double[secret_word.length()][26];

        calculateProbabilities();
    }

    private void calculateProbabilities() {
        for (int i = 0; i < secret_word.length(); ++i) {
            for (int j = 0; j < 26; ++j)
                probabilities[i][j] = 0.0;

            for (String s : possible_words)
                probabilities[i][s.charAt(i) - 'A'] += 1.0 / possible_words.size();
        }
    }

    public int guessLetter(int position, char letter) throws InvalidPositionException {
        if (position < 0 || position >= secret_word.length())
            throw new InvalidPositionException("Not a valid position for this word!");
        if (current_guess[position] != '_')
            throw new InvalidPositionException("You have already found the correct letter for this position!");

        if (secret_word.charAt(position) == letter) {
            int score;

            if (probabilities[position][letter - 'A'] >= 0.6)
                score = 5;
            else if (probabilities[position][letter - 'A'] >= 0.4)
                score = 10;
            else if (probabilities[position][letter - 'A'] >= 0.25)
                score = 15;
            else
                score = 30;

            current_guess[position] = letter;

            possible_words.removeIf(s -> s.charAt(position) != letter);
            calculateProbabilities();

            return score;
        }
        else {
            possible_words.removeIf(s -> s.charAt(position) == letter);
            calculateProbabilities();

            return -15;
        }
    }

    public String reveal() {
        return secret_word;
    }

    public String getGuess() {
        return new String(current_guess);
    }

    public boolean found() {
        return secret_word.equals(new String(current_guess));
    }

    public List<PositionProbabilities> getProbabilities() {
        List<PositionProbabilities> sorted_probabilities = new ArrayList<>();

        for (int i = 0; i < secret_word.length(); ++i) {
            List<Character> temp = new ArrayList<>();

            final int help = i;

            for (int j = 0; j < 26; ++j)
                if (probabilities[i][j] != 0.0)
                    temp.add((char)(j + 'A'));

            Collections.sort(temp, (n1, n2) -> (int) (probabilities[help][n2 - 'A']*10000 - probabilities[help][n1 - 'A']*10000));

            String str = temp.toString().substring(1, 3 * temp.size() - 1);
            sorted_probabilities.add(new PositionProbabilities(i, str));
        }

        return sorted_probabilities;
    }
}