package com.alpiq.autocomplete.trie;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@RequiredArgsConstructor
class TrieNode<E extends Word> {
    private final String word;
    private int height;
    private final Map<Character, TrieNode<E>> children = new HashMap<>();
    private final Set<E> values = new HashSet<>();

}
