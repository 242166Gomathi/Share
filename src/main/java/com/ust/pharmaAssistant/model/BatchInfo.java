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
 * Model class representing batch information in the PharmaAssistant application.
 * This class represents the details of a batch, including batch code, medicine code, weight, price,
 * medicine type code, shipping charge, care level, and refrigeration requirement.
 * It is annotated with JPA annotations for mapping to the database and Lombok annotations for generating
 * boilerplate code.
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BatchInfo implements Serializable {

    /** Default serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /** Unique identifier for the batch. */
    @Id
    private String batchCode;

    /** Medicine code associated with the batch. */
    private String medicineCode;

    /** Weight of the batch. */
    private double weight;

    /** Price of the batch. */
    private double price;

    /** Medicine type code associated with the batch. */
    private String medicineTypeCode;

    /** Shipping charge associated with the batch. */
    private double shippingCharge;

    /** Care level of the batch. */
    private String careLevel;

    /** Refrigeration requirement for the batch. */
    private String refrigeration;

    /**
     * Constructs a BatchInfo object with the specified parameters.
     *
     * @param batchCode       The batch code.
     * @param medicineCode    The medicine code.
     * @param weight          The weight of the batch.
     * @param price           The price of the batch.
     * @param medicineTypeCode The medicine type code.
     */
    public BatchInfo(String batchCode, String medicineCode, double weight, double price, String medicineTypeCode) {
        this.batchCode = batchCode;
        this.medicineCode = medicineCode;
        this.weight = weight;
        this.price = price;
        this.medicineTypeCode = medicineTypeCode;
        this.refrigeration = determineRefrigeration(weight); // Set refrigeration automatically
    }

    /**
     * Determines the refrigeration requirement based on the weight of the batch.
     *
     * @param weight The weight of the batch.
     * @return The refrigeration requirement (Yes/No).
     */
    private String determineRefrigeration(double weight) {
        return (weight > 500) ? "Yes" : "No"; // Example logic: Refrigeration required if weight > 500
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the o argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BatchInfo batchInfo)) return false;
        return Double.compare(batchInfo.getWeight(), getWeight()) == 0 &&
                Double.compare(batchInfo.getPrice(), getPrice()) == 0 &&
                Double.compare(batchInfo.getShippingCharge(), getShippingCharge()) == 0 &&
                Objects.equals(getBatchCode(), batchInfo.getBatchCode()) &&
                Objects.equals(getMedicineCode(), batchInfo.getMedicineCode()) &&
                Objects.equals(getMedicineTypeCode(), batchInfo.getMedicineTypeCode()) &&
                Objects.equals(getCareLevel(), batchInfo.getCareLevel()) &&
                Objects.equals(getRefrigeration(), batchInfo.getRefrigeration());
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getBatchCode(), getMedicineCode(), getWeight(), getPrice(), getMedicineTypeCode(), getShippingCharge(), getCareLevel(), getRefrigeration());
    }

    /**
     * Returns a string representation of the object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "BatchInfo{" +
                "batchCode='" + batchCode + '\'' +
                ", medicineCode='" + medicineCode + '\'' +
                ", weight=" + weight +
                ", price=" + price +
                ", medicineTypeCode='" + medicineTypeCode + '\'' +
                ", shippingCharge=" + shippingCharge +
                ", careLevel='" + careLevel + '\'' +
                ", refrigeration='" + refrigeration + '\'' +
                '}';
    }
}
