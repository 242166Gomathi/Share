package com.ust.pharmaAssistant.repository;

import com.ust.pharmaAssistant.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, String> {
    // Add custom query methods if needed
}
