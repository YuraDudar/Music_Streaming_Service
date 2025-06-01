package com.searchservice.proj.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "#{@elasticsearchIndexNamer.getAlbumIndexName()}")
public class AlbumDocument {

    @Id
    private String id;

    @Field({
            @Field(type = FieldType.Text, analyzer = "standard"),
            @Field(name = "keyword", type = FieldType.Keyword, ignore_above = 256)
    })
    private String title;

    @Field(type = FieldType.Keyword)
    private String titleSort;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate releaseDate;

    @Field(type = FieldType.Keyword)
    private String albumType;

    @Field(type = FieldType.Keyword, index = false)
    private String s3CoverKey;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<TrackDocument.ArtistInfoInTrack> artists;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<TrackDocument.GenreInfoInTrack> genres;

    @Field(type = FieldType.Integer)
    private Integer trackCount;

    @Field(type = FieldType.Float)
    private Double popularityScore;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String searchTextAll;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime updatedAt;
}