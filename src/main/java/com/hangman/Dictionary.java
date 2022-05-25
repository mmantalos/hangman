package com.hangman;

import java.util.List;
import java.util.Random;

public class Dictionary {
    private List<String> word_list;
    private int word_count;
    private double percentSix, percentSevenNine, percentTenMore;

    Dictionary(List<String> words) {
        word_list  = words;
        word_count = words.size();

        initStats();
    }

    private void initStats() {
        for (String s : word_list) {
            int length = s.length();

            if (length == 6)
                ++percentSix;
            else if (length <= 9)
                ++percentSevenNine;
            else
                ++percentTenMore;
        }

        percentSix       = ((int) (percentSix       * 10000 / word_count)) / 100.0;
        percentSevenNine = ((int) (percentSevenNine * 10000 / word_count)) / 100.0;
        percentTenMore   = ((int) (percentTenMore   * 10000 / word_count)) / 100.0;
    }

    public SecretWord getSecretWord() {
        Random rand = new Random();
        String secret_word = word_list.get(rand.nextInt(word_count));

        return new SecretWord(secret_word, word_list);

    }

    public double[] getStats() {
        return new double[] { word_count, percentSix, percentSevenNine, percentTenMore } ;
    }
}