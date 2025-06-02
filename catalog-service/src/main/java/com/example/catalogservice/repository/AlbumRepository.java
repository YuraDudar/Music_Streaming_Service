package com.example.catalogservice.repository;

import com.example.catalogservice.entity.AlbumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, Long>, JpaSpecificationExecutor<AlbumEntity> {
    // Для GET /artists/{artistId}/albums (поиск альбомов по ID артиста)
    Page<AlbumEntity> findByArtists_Id(Long artistId, Pageable pageable);
}