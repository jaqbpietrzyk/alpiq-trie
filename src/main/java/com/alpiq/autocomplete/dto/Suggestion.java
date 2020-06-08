package com.alpiq.autocomplete.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Suggestion {
    private String name;
    private double score;
}
