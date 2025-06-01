package com.example.catalogservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "artists")
public class ArtistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "bio")
    private String bio;

    @Column(name = "s3_artist_photo_key")
    private String s3ArtistPhotoKey;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Связи остаются такими же, как мы обсуждали
    @ManyToMany(mappedBy = "artists")
    private Set<TrackEntity> tracks = new HashSet<>();

    @ManyToMany(mappedBy = "artists")
    private Set<AlbumEntity> albums = new HashSet<>();
}