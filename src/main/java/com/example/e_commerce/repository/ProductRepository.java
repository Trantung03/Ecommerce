package com.example.e_commerce.repository;

import com.example.e_commerce.dto.response.ProductListResponse;
import com.example.e_commerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT p
        FROM Product p
        WHERE (p.category.id = :categoryId OR :categoryId IS NULL)
        AND (p.basePrice >= :minPrice OR :minPrice IS NULL)
        AND (p.basePrice <= :maxPrice OR :maxPrice IS NULL)
        AND p.isActive = true
  """)
    Page<Product> findProductCatalog(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            Pageable pageable
    );
}
