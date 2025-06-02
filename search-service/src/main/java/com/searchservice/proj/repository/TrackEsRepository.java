package com.searchservice.proj.repository;

import com.searchservice.proj.document.TrackDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackEsRepository extends ElasticsearchRepository<TrackDocument, String> {
}