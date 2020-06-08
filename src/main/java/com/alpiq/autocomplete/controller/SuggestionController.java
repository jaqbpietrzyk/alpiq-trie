package com.alpiq.autocomplete.controller;

import com.alpiq.autocomplete.dto.Suggestion;
import com.alpiq.autocomplete.trie.CityLocation;
import com.alpiq.autocomplete.trie.Result;
import com.alpiq.autocomplete.trie.Trie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@Slf4j
public class SuggestionController {

    private final Trie<CityLocation> trie;

    public SuggestionController(Trie<CityLocation> trie) {
        this.trie = trie;
    }

    @GetMapping("suggestions")
    public PagedModel<EntityModel<Suggestion>> suggest(@RequestParam("q") String term, Pageable pageable, PagedResourcesAssembler<Suggestion> assembler) {
        List<Result<CityLocation>> results = trie.searchByPrefix(term.toLowerCase());
        List<Suggestion> suggestions = results.stream()
                .map(r -> new Suggestion(
                        String.format("%s %s", r.getValue().getCity(), r.getValue().getCountry().toUpperCase()),
                        BigDecimal.valueOf(r.getScore()).setScale(2, RoundingMode.HALF_UP).doubleValue()))
                .collect(Collectors.toList());
        int startIdx = (int) pageable.getOffset();
        int endIdx = Math.min((startIdx + pageable.getPageSize()), suggestions.size());
        log.info("Suggestions={} StartIdx={} EndIdx{}", suggestions.size(), startIdx, endIdx);

        Page<Suggestion> page = new PageImpl<>(suggestions.subList(startIdx, endIdx), pageable, suggestions.size());
        return assembler.toModel(page, linkTo(SuggestionController.class).slash("/suggestions").withSelfRel());
    }
}
