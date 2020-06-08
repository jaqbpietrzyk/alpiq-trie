package com.alpiq.autocomplete.trie;

import lombok.Value;

@Value
public class Result<E> {
    E value;
    double score;
}
