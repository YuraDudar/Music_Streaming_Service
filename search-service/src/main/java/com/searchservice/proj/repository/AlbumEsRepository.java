package com.searchservice.proj.repository;
import com.searchservice.proj.document.AlbumDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumEsRepository extends ElasticsearchRepository<AlbumDocument, String> {
}