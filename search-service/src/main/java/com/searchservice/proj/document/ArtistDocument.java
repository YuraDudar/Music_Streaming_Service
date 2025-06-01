package com.searchservice.proj.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "#{@elasticsearchIndexNamer.getArtistIndexName()}")
public class ArtistDocument {

    @Id
    private String id;

    @Field({
            @Field(type = FieldType.Text, analyzer = "standard"),
            @Field(name = "keyword", type = FieldType.Keyword, ignore_above = 256)
    })
    private String name;

    @Field(type = FieldType.Keyword)
    private String nameSort;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String bio;

    @Field(type = FieldType.Keyword, index = false)
    private String s3PhotoKey;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<TrackDocument.GenreInfoInTrack> topGenres;

    @Field(type = FieldType.Float)
    private Double popularityScore;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String searchTextAll;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime updatedAt;
}