package com.alpiq.autocomplete.trie;

public interface Word {
    String getWord();
    double calculateScore();
}
