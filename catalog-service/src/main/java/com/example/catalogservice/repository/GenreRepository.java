package com.example.catalogservice.repository;

import com.example.catalogservice.entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Integer>, JpaSpecificationExecutor<GenreEntity> {
}