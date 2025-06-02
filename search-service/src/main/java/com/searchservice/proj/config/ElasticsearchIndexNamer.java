package com.searchservice.proj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("elasticsearchIndexNamer")
public class ElasticsearchIndexNamer {
    @Value("${app.elasticsearch.index.tracks}") private String trackIndexName;
    @Value("${app.elasticsearch.index.albums}") private String albumIndexName;
    @Value("${app.elasticsearch.index.artists}") private String artistIndexName;

    public String getTrackIndexName() { return trackIndexName; }
    public String getAlbumIndexName() { return albumIndexName; }
    public String getArtistIndexName() { return artistIndexName; }
}