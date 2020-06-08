package com.alpiq.autocomplete.score;

public class ScoreCalculator {

    public static double score(double val, double min, double max) {
        val = Math.max(Math.min(val, max), min);
        return ((val - min) / (max - min));
    }
}
