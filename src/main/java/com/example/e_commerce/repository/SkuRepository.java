package com.example.e_commerce.repository;

import com.example.e_commerce.entity.Sku;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SkuRepository extends JpaRepository<Sku,Long> {

    List<Sku> findByProduct_Id(Long productId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Sku s WHERE s.id = :skuId")
    Optional<Sku> findByIdWithLock(@Param("skuId") Long skuId);
}
