package com.ust.pharmaAssistant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
/**
 * Model class representing shipping information in the PharmaAssistant application.
 * This class represents the shipping details for different medicine types and weight ranges,
 * including the medicine type code, weight range, and shipping charge.
 * It is annotated with JPA annotations for mapping to the database and Lombok annotations for generating
 * boilerplate code.
 */
@Getter
@Setter
@Entity
public class ShippingMaster implements Serializable {

    /** Default serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String medicineTypeCode;
    private String weightRange;
    private double shippingCharge;

    // Equals, hashCode, and toString methods are overridden for proper object comparison and logging purposes.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShippingMaster that)) return false;
        return Double.compare(that.getShippingCharge(), getShippingCharge()) == 0 &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getMedicineTypeCode(), that.getMedicineTypeCode()) &&
                Objects.equals(getWeightRange(), that.getWeightRange());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMedicineTypeCode(), getWeightRange(), getShippingCharge());
    }

    @Override
    public String toString() {
        return "ShippingMaster{" +
                "id=" + id +
                ", medicineTypeCode='" + medicineTypeCode + '\'' +
                ", weightRange='" + weightRange + '\'' +
                ", shippingCharge=" + shippingCharge +
                '}';
    }
}