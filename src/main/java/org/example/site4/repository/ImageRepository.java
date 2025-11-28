package org.example.site4.repository;

import org.example.site4.domain.Image;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByUserId(Long userId);
    List<Image> findByCategoryId(Long categoryId);

    @Query("SELECT i FROM Image i LEFT JOIN FETCH i.user ORDER BY i.createdAt DESC")
    List<Image> findAllWithUsers();

    @Query("SELECT i FROM Image i WHERE i.title LIKE %:query% OR i.description LIKE %:query%")
    List<Image> searchByTitleOrDescription(@Param("query") String query);

    List<Image> findByOrderByCreatedAtDesc(Pageable pageable);
}