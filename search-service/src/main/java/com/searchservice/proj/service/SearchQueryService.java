package com.searchservice.proj.service;

import com.searchservice.proj.dto.SearchResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchQueryService {
    SearchResponseDto search(String query, List<String> types, Pageable pageable);
}