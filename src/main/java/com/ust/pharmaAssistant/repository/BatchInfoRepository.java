package com.ust.pharmaAssistant.repository;

import com.ust.pharmaAssistant.model.BatchInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing BatchInfo entities.
 */
@Repository
public interface BatchInfoRepository extends JpaRepository<BatchInfo, String> {
    /**
     * Checks if a batch with the given batch code exists.
     * @param batchCode The batch code to check.
     * @return true if a batch with the given code exists, false otherwise.
     */
    boolean existsByBatchCode(String batchCode);
//    boolean existsByMedicineCode(String medicineCode);
}
