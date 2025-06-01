package com.searchservice.proj.repository;

import com.searchservice.proj.document.ArtistDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistEsRepository extends ElasticsearchRepository<ArtistDocument, String> {
}