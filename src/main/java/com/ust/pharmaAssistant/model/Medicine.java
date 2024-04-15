package com.ust.pharmaAssistant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model class representing medicine information in the PharmaAssistant application.
 * This class represents the details of a medicine, including the medicine code and name.
 * It is annotated with JPA annotations for mapping to the database and Lombok annotations for generating
 * boilerplate code.
 */
@Getter
@Setter
@Entity
public class Medicine implements Serializable {

    /** Default serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /** Unique identifier for the medicine. */
    @Id
    private String medicineCode;
    private String medicineName;

    /**
     * Default constructor for Medicine.
     */
    public Medicine() {
    }

    // Equals, hashCode, and toString methods are overridden for proper object comparison and logging purposes.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.ust.pharmaAssistant.model.Medicine medicine = (com.ust.pharmaAssistant.model.Medicine) o;
        return Objects.equals(getMedicineCode(), medicine.getMedicineCode()) &&
                Objects.equals(getMedicineName(), medicine.getMedicineName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMedicineCode(), getMedicineName());
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "medicineCode='" + medicineCode + '\'' +
                ", medicineName='" + medicineName + '\'' +
                '}';
    }
}
