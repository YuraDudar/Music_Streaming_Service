package com.searchservice.proj.controller;

import com.searchservice.proj.dto.SearchResponseDto;
import com.searchservice.proj.service.SearchQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchQueryService searchQueryService;

    @GetMapping
    public ResponseEntity<SearchResponseDto> search(
            @RequestParam(name = "q") String query,
            @RequestParam(name = "type", required = false) Optional<String> typesOpt,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "offset", defaultValue = "0") int offset
    ) {
        int page = offset / limit;
        Pageable pageable = PageRequest.of(page, limit);

        List<String> types = typesOpt
                .map(s -> Arrays.asList(s.split(",")))
                .orElse(Collections.emptyList());

        SearchResponseDto response = searchQueryService.search(query, types, pageable);
        return ResponseEntity.ok(response);
    }
}