package com.example.catalogservice.repository; // Замените на ваш пакет

import com.example.catalogservice.entity.TrackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface TrackRepository extends JpaRepository<TrackEntity, Long>, JpaSpecificationExecutor<TrackEntity> {

    Page<TrackEntity> findByAlbum_Id(Long albumId, Pageable pageable);

    Page<TrackEntity> findByArtists_Id(Long artistId, Pageable pageable);

}