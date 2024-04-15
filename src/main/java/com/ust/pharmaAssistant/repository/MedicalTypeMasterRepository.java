package com.ust.pharmaAssistant.repository;

import com.ust.pharmaAssistant.model.MedicalTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalTypeMasterRepository extends JpaRepository<MedicalTypeMaster, String> {
    // Add custom query methods if needed
}
