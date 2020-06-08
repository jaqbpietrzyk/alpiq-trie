package com.alpiq.autocomplete.trie;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import static com.alpiq.autocomplete.score.ScoreCalculator.score;

@Component
public class ScoreTrie<E extends Word> implements Trie<E> {
    private final TrieNode<E> root = new TrieNode<>("dummy");

    @Override
    public void insert(E e) {
        TrieNode<E> current = root;
        String word = e.getWord();
        for (int i = 0; i < word.length(); i++) {
            String currentWord = word.substring(0, i + 1);
            current = current.getChildren().computeIfAbsent(word.charAt(i), m -> new TrieNode<>(currentWord));
            current.setHeight(Math.max(word.length(), current.getHeight()));
        }
        current.getValues().add(e);
    }

    @Override
    public List<Result<E>> searchByPrefix(String prefix) {
        Optional<TrieNode<E>> node = searchForInternalNode(prefix);
        if (node.isEmpty()) {
            return List.of();
        }
        Queue<Map<Character, TrieNode<E>>> queue = new LinkedList<>();
        int maxLevel = node.get().getHeight();
        List<Result<E>> results = new ArrayList<>();
        node.get().getValues().forEach(v -> {
            double overallScore = calculateScores(prefix, maxLevel, v);
            results.add(new Result<>(v, overallScore));
        });
        queue.add(node.get().getChildren());
        while (!queue.isEmpty()) {
            Map<Character, TrieNode<E>> childs = queue.poll();
            for (Map.Entry<Character, TrieNode<E>> entry : childs.entrySet()) {
                queue.add(entry.getValue().getChildren());
                Set<E> values = entry.getValue().getValues();
                values.forEach(v -> {
                    double overallScore = calculateScores(prefix, maxLevel, v);
                    results.add(new Result<>(v, overallScore));
                });
            }
        }
        results.sort((s1, s2) -> Double.compare(s2.getScore(), s1.getScore()));
        return results;
    }

    private double calculateScores(String word, int maxLevel, E v) {
        double internalScore = 1 - score(v.getWord().length() - word.length(), 0, maxLevel);
        double externalScore = v.calculateScore();
        return score(internalScore + externalScore, 0, 2);
    }

    private Optional<TrieNode<E>> searchForInternalNode(String word) {
        TrieNode<E> current = root;
        for (char c : word.toCharArray()) {
            TrieNode<E> node = current.getChildren().get(c);
            if (node == null) {
                return Optional.empty();
            }
            current = node;
        }
        return Optional.of(current);
    }
}
