package com.searchservice.proj.service.impl;

import com.searchservice.proj.document.AlbumDocument;
import com.searchservice.proj.document.ArtistDocument;
import com.searchservice.proj.document.TrackDocument;
import com.searchservice.proj.dto.SearchResponseDto;
import com.searchservice.proj.dto.response.AlbumSearchResultDto;
import com.searchservice.proj.dto.response.ArtistSearchResultDto;
import com.searchservice.proj.dto.response.TrackSearchResultDto;
import com.searchservice.proj.config.ElasticsearchIndexNamer;
import com.searchservice.proj.service.S3UrlResolver;
import com.searchservice.proj.service.SearchQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeQuery;
import org.springframework.data.elasticsearch.core.query.NativeQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchQueryServiceImpl implements SearchQueryService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchIndexNamer indexNamer; 
    private final S3UrlResolver s3UrlResolver; 

    @Override
    public SearchResponseDto search(String query, List<String> types, Pageable pageable) {
        log.info("Performing search for query: '{}', types: {}, pageable: {}", query, types, pageable);

        List<TrackSearchResultDto> tracks = new ArrayList<>();
        List<AlbumSearchResultDto> albums = new ArrayList<>();
        List<ArtistSearchResultDto> artists = new ArrayList<>();

        List<String> targetIndices = new ArrayList<>();
        boolean searchTracks = types == null || types.isEmpty() || types.contains("track");
        boolean searchAlbums = types == null || types.isEmpty() || types.contains("album");
        boolean searchArtists = types == null || types.isEmpty() || types.contains("artist");

        if (searchTracks) targetIndices.add(indexNamer.getTrackIndexName());
        if (searchAlbums) targetIndices.add(indexNamer.getAlbumIndexName());
        if (searchArtists) targetIndices.add(indexNamer.getArtistIndexName());

        if (targetIndices.isEmpty() || query == null || query.isBlank()) {
            return SearchResponseDto.builder()
                    .tracks(Collections.emptyList())
                    .albums(Collections.emptyList())
                    .artists(Collections.emptyList())
                    .build();
        }

        
        
        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(q -> q
                        .multiMatch(mq -> mq
                                .query(query)
                                .fields("searchTextAll^1.0", "title^3.0", "name^3.0", 
                                        "album.title^2.0", "artists.name^2.0", "genres.name^1.5", "bio^0.5")
                                .type(org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.BEST_FIELDS) 
                                .operator(Operator.OR) 
                                .fuzziness(Fuzziness.AUTO) 
                                .prefixLength(1) 
                        )
                )
                .withPageable(pageable) 
                
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<strong>").postTags("</strong>"),
                        new HighlightBuilder.Field("name").preTags("<strong>").postTags("</strong>"),
                        new HighlightBuilder.Field("album.title").preTags("<strong>").postTags("</strong>"),
                        new HighlightBuilder.Field("artists.name").preTags("<strong>").postTags("</strong>")
                )
                .withIndices(targetIndices.toArray(new String[0])) 
                
                .build();

        SearchHits<?> searchHits = elasticsearchOperations.search(searchQuery, Object.class, 
                org.springframework.data.elasticsearch.core.mapping.IndexCoordinates.of(targetIndices.toArray(new String[0])));

        for (SearchHit<?> hit : searchHits) {
            String indexName = hit.getIndex();
            Map<String, List<String>> highlightFields = hit.getHighlightFields(); 

            if (indexNamer.getTrackIndexName().equals(indexName) && searchTracks) {
                TrackDocument doc = (TrackDocument) elasticsearchOperations.getElasticsearchConverter().read(TrackDocument.class, hit.getDocument());
                tracks.add(mapToTrackResult(doc, highlightFields));
            } else if (indexNamer.getAlbumIndexName().equals(indexName) && searchAlbums) {
                AlbumDocument doc = (AlbumDocument) elasticsearchOperations.getElasticsearchConverter().read(AlbumDocument.class, hit.getDocument());
                albums.add(mapToAlbumResult(doc, highlightFields));
            } else if (indexNamer.getArtistIndexName().equals(indexName) && searchArtists) {
                ArtistDocument doc = (ArtistDocument) elasticsearchOperations.getElasticsearchConverter().read(ArtistDocument.class, hit.getDocument());
                artists.add(mapToArtistResult(doc, highlightFields));
            }
        }

        return SearchResponseDto.builder()
                .tracks(tracks)
                .albums(albums)
                .artists(artists)
                .build();
    }

    
    private TrackSearchResultDto mapToTrackResult(TrackDocument doc, Map<String, List<String>> highlightFields) {
        String highlightedTitle = getHighlight(highlightFields, "title", doc.getTitle());

        TrackSearchResultDto.AlbumInfoForSearch albumInfo = null;
        if (doc.getAlbum() != null) {
            albumInfo = TrackSearchResultDto.AlbumInfoForSearch.builder()
                    .id(doc.getAlbum().getId())
                    .title(getHighlight(highlightFields, "album.title", doc.getAlbum().getTitle()))
                    .s3CoverUrl(s3UrlResolver.resolveUrl(doc.getAlbum().getS3CoverKey()))
                    .build();
        }

        List<TrackSearchResultDto.ArtistInfoForSearch> artistInfos = Collections.emptyList();
        if (doc.getArtists() != null) {
            artistInfos = doc.getArtists().stream()
                    .map(a -> TrackSearchResultDto.ArtistInfoForSearch.builder()
                            .id(a.getId())
                            .name(getHighlight(highlightFields, "artists.name", a.getName())) 
                            .build())
                    .collect(Collectors.toList());
        }

        return TrackSearchResultDto.builder()
                .id(doc.getId())
                .title(highlightedTitle)
                .durationMs(doc.getDurationMs())
                .audioFileS3Url(s3UrlResolver.resolveUrl(doc.getS3AudioKey()))
                .isExplicit(doc.getIsExplicit())
                .releaseDate(doc.getReleaseDate())
                .album(albumInfo)
                .artists(artistInfos)
                
                .build();
    }

    private AlbumSearchResultDto mapToAlbumResult(AlbumDocument doc, Map<String, List<String>> highlightFields) {
        List<TrackSearchResultDto.ArtistInfoForSearch> artistInfos = Collections.emptyList();
        if (doc.getArtists() != null) {
            artistInfos = doc.getArtists().stream()
                    .map(a -> TrackSearchResultDto.ArtistInfoForSearch.builder()
                            .id(a.getId())
                            .name(getHighlight(highlightFields, "artists.name", a.getName()))
                            .build())
                    .collect(Collectors.toList());
        }
        return AlbumSearchResultDto.builder()
                .id(doc.getId())
                .title(getHighlight(highlightFields, "title", doc.getTitle()))
                .albumType(doc.getAlbumType())
                .releaseDate(doc.getReleaseDate())
                .s3CoverUrl(s3UrlResolver.resolveUrl(doc.getS3CoverKey()))
                .artists(artistInfos)
                .build();
    }

    private ArtistSearchResultDto mapToArtistResult(ArtistDocument doc, Map<String, List<String>> highlightFields) {
        return ArtistSearchResultDto.builder()
                .id(doc.getId())
                .name(getHighlight(highlightFields, "name", doc.getName()))
                .s3PhotoUrl(s3UrlResolver.resolveUrl(doc.getS3PhotoKey()))
                .build();
    }

    
    private String getHighlight(Map<String, List<String>> highlightFields, String fieldName, String defaultValue) {
        if (highlightFields != null && highlightFields.containsKey(fieldName) && !highlightFields.get(fieldName).isEmpty()) {
            return highlightFields.get(fieldName).get(0); 
        }
        return defaultValue;
    }
}