package com.ust.pharmaAssistant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
/**
 * Model class representing medical type information in the PharmaAssistant application.
 * This class represents the master data for medical types, including the medicine type code and name.
 * It is annotated with JPA annotations for mapping to the database and Lombok annotations for generating
 * boilerplate code.
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class MedicalTypeMaster implements Serializable {

    /** Default serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /** Unique identifier for the medical type. */
    @Id
    private String medicineTypeCode;
    private String medicineTypeName;

    // Equals, hashCode, and toString methods are overridden for proper object comparison and logging purposes.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalTypeMaster that = (MedicalTypeMaster) o;
        return Objects.equals(getMedicineTypeCode(), that.getMedicineTypeCode()) &&
                Objects.equals(getMedicineTypeName(), that.getMedicineTypeName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMedicineTypeCode(), getMedicineTypeName());
    }

    @Override
    public String toString() {
        return "MedicalTypeMaster{" +
                "medicineTypeCode='" + medicineTypeCode + '\'' +
                ", medicineTypeName='" + medicineTypeName + '\'' +
                '}';
    }
}