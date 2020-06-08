package com.alpiq.autocomplete.trie;

import java.util.List;

public interface Trie<E extends Word> {
    void insert(E e);
    List<Result<E>> searchByPrefix(String prefix);
}
