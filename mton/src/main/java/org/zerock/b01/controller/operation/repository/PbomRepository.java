package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.operation.Pbom;

import java.util.List;
import java.util.Optional;

public interface PbomRepository extends JpaRepository<Pbom, String> {
    @Query(value = "SELECT p.pbomId FROM Pbom p WHERE p.pbomId LIKE ?1% ORDER BY p.pbomId DESC LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);

    @Query(value = "SELECT p FROM Pbom p WHERE p.product.prodId = :prodId")
    List<Pbom> findAllByProdId(String prodId);

    @Query(value = "SELECT p FROM Pbom p WHERE p.product.prodId = :prodId AND p.material.matId = :matId")
    Optional<Pbom> findByProdAndMatId(@Param("prodId") String prodId, @Param("matId") String matId);
}
