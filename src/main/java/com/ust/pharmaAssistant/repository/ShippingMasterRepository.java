package com.ust.pharmaAssistant.repository;

import com.ust.pharmaAssistant.model.ShippingMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ShippingMasterRepository extends JpaRepository<ShippingMaster, Long> {
    Optional<ShippingMaster> findByMedicineTypeCodeAndWeightRange(String medicineTypeCode, String weightRange);
    // Add custom query methods if needed
}
