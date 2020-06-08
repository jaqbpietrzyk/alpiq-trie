package com.alpiq.autocomplete.trie;

import lombok.NonNull;
import lombok.Value;

import static com.alpiq.autocomplete.score.ScoreCalculator.score;

@Value
public class CityLocation implements Word {
    public static final int POPULATION_WEIGHT = 5;
    public static final int POPULATION_MAX = 22315474;
    @NonNull
    String city;
    @NonNull
    String country;
    @NonNull
    int population;

    @Override
    public String getWord() {
        return city;
    }

    @Override
    public double calculateScore() {
        return score(population * POPULATION_WEIGHT, 0, POPULATION_MAX);
    }
}
