package org.delcom.app.repositories;

import org.delcom.app.entities.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, UUID> {

    // Mencari semua cash flow berdasarkan user ID
    List<CashFlow> findByUserId(UUID userId);

    // Mencari cash flow berdasarkan ID dan user ID
    Optional<CashFlow> findByIdAndUserId(UUID id, UUID userId);

    // Mencari cash flow berdasarkan user ID dengan pencarian
    @Query("SELECT c FROM CashFlow c WHERE c.userId = :userId AND " +
           "(LOWER(c.source) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.label) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<CashFlow> findByUserIdWithSearch(@Param("userId") UUID userId, @Param("search") String search);

    // Mendapatkan semua label unik berdasarkan user ID
    @Query("SELECT DISTINCT c.label FROM CashFlow c WHERE c.userId = :userId ORDER BY c.label")
    List<String> findDistinctLabelsByUserId(@Param("userId") UUID userId);

    // Menghapus cash flow berdasarkan ID dan user ID
    void deleteByIdAndUserId(UUID id, UUID userId);
}