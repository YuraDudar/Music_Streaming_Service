package com.searchservice.proj.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "#{@elasticsearchIndexNamer.getTrackIndexName()}")
public class TrackDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Keyword, name = "title.keyword", ignore_above = 256)
    private String titleKeyword;

    @Field(type = FieldType.Keyword)
    private String titleSort;

    @Field(type = FieldType.Integer)
    private Integer durationMs;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class GenreInfoInTrack {
        @Field(type = FieldType.Keyword)
        private String id;

        @Field(type = FieldType.Text, analyzer = "standard")
        private String name;

        @Field(type = FieldType.Keyword, name = "name.keyword", ignore_above = 256)
        private String nameKeyword;
    }
}