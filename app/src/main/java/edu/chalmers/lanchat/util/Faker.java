package edu.chalmers.lanchat.util;

import java.util.Random;

/**
 * Factory class which creates simple, random, fake data
 */
public class Faker {
    private static final String alphabet = "abcdefghijklmnopqrstuvxyzåäö";

    private final Random random;

    private Integer seed;

    public Faker() {
        seed = null;
        random = new Random();
    }

    public Faker(int seed) {
        seed = seed;
        random = new Random(seed);
    }

    public String sentence(int min, int max) {
        String sentence = words(min, max);

        // Start with a big letter
         sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1);

        return sentence += ".";
    }

    public String words(int min, int max) {
        int nrWords = random.nextInt(max - min) + min;
        StringBuilder words = new StringBuilder();
        for (int i = 0; i < nrWords; i++) {
            words.append(word(3, 9) + " ");
        }
        return words.toString().trim();
    }

    public String word(int min, int max) {
        int nrLetters = random.nextInt(max - min) + min;
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < nrLetters; i++) {
            word.append(letter());
        }
        return word.toString();
    }

    public String letter() {
        int index = random.nextInt(alphabet.length());
        return alphabet.substring(index, index + 1);
    }
}
