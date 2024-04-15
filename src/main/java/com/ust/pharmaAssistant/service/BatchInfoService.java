package com.ust.pharmaAssistant.service;

import com.ust.pharmaAssistant.exception.PharmaBusinessException;
import com.ust.pharmaAssistant.model.BatchInfo;
import com.ust.pharmaAssistant.model.MedicalTypeMaster;
import com.ust.pharmaAssistant.model.Medicine;
import com.ust.pharmaAssistant.model.ShippingMaster;
import com.ust.pharmaAssistant.repository.BatchInfoRepository;
import com.ust.pharmaAssistant.repository.MedicalTypeMasterRepository;
import com.ust.pharmaAssistant.repository.MedicineRepository;
import com.ust.pharmaAssistant.repository.ShippingMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service class responsible for managing BatchInfo operations in the PharmaAssistant application.
 * This class handles the logic for adding a batch, validating batch information, checking for duplicates,
 * verifying the existence of medicine codes, calculating shipping charges, and determining care levels.
 */
@Service
public class BatchInfoService {

    /** Logger instance for logging purposes. */
    private static final Logger logger = LoggerFactory.getLogger(BatchInfoService.class);

    /** Repository for interacting with BatchInfo entities. */
    @Autowired
    private BatchInfoRepository batchInfoRepository;

    /** Repository for interacting with MedicalTypeMaster entities. */
    @Autowired
    private MedicalTypeMasterRepository medicalTypeMasterRepository;

    /** Repository for interacting with Medicine entities. */
    @Autowired
    private MedicineRepository medicineRepository;

    /** Repository for interacting with ShippingMaster entities. */
    @Autowired
    private ShippingMasterRepository shippingMasterRepository;

    /**
     * Adds a new batch to the system.
     * @param batchInfo The batch information to add.
     * @return true if the batch is added successfully, false otherwise.
     * @throws PharmaBusinessException if an error occurs during batch addition.
     */
    public boolean addBatch(BatchInfo batchInfo) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Add Batch");

        validateBatchInfo(batchInfo);
        checkForDuplicates(batchInfo);
        checkIfMedicineCodeExists(batchInfo.getMedicineCode());
        calculateShippingCharge(batchInfo);
        calculateCareLevel(batchInfo);

        try {
            batchInfoRepository.save(batchInfo);
            logger.info("Batch added successfully.");
            stopWatch.stop();
            logger.info("Time taken to add batch: {} ms", stopWatch.getTotalTimeMillis());
            return true;
        } catch (Exception e) {
            logger.error("An error occurred while adding batch: {}", e.getMessage());
            throw new PharmaBusinessException(500, "General system Error");
        }
    }


    /**
     * Validates the batch information before adding it to the system.
     * @param batchInfo The batch information to validate.
     * @throws PharmaBusinessException if any validation error occurs.
     */
    private void validateBatchInfo(BatchInfo batchInfo) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Validate Batch Info");

        if (StringUtils.isEmpty(batchInfo.getMedicineCode())) {
            throw new PharmaBusinessException(510, "Medicine code is required");
        }
        if (StringUtils.isEmpty(batchInfo.getBatchCode())) {
            throw new PharmaBusinessException(513, "Batch code is required");
        }
        if (!isValidBatchCode(batchInfo.getBatchCode())) {
            throw new PharmaBusinessException(513, "Batch code should be in the format 'BTC-1234'");
        }
        if (batchInfo.getWeight() < 100) {
            throw new PharmaBusinessException(512, "Batch Weight should be greater than 100");
        }
        if (StringUtils.isEmpty(batchInfo.getRefrigeration())) {
            throw new PharmaBusinessException(516, "Refrigeration requirement is mandatory");
        }

        stopWatch.stop();
        logger.info("Time taken to validate batch info: {} ms", stopWatch.getTotalTimeMillis());
    }

    /**
     * Checks if the provided batch code follows the specified format.
     * @param batchCode The batch code to validate.
     * @return true if the batch code is in the correct format, false otherwise.
     */
    private boolean isValidBatchCode(String batchCode) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Validate Batch Code");

        String regex = "^BTC-\\d{4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(batchCode);
        boolean isValid = matcher.matches();

        stopWatch.stop();
        logger.info("Time taken to validate batch code: {} ms", stopWatch.getTotalTimeMillis());
        return isValid;
    }

    /**
     * Checks for duplicate batch codes in the repository.
     * @param batchInfo The batch information to check for duplicates.
     * @throws PharmaBusinessException if a duplicate batch code is found.
     */
    void checkForDuplicates(BatchInfo batchInfo) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Check for Duplicates");

        if (batchInfoRepository.existsByBatchCode(batchInfo.getBatchCode())) {
            throw new PharmaBusinessException(511, "Batch Code already exists");
        }

        stopWatch.stop();
        logger.info("Time taken to check for duplicates: {} ms", stopWatch.getTotalTimeMillis());
    }

    /**
     * Checks if the provided medicine code exists in the repository.
     * @param medicineCode The medicine code to check.
     * @throws PharmaBusinessException if the medicine code does not exist.
     */
    void checkIfMedicineCodeExists(String medicineCode) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Check Medicine Code Existence");

        Optional<Medicine> medicine = medicineRepository.findById(medicineCode);
        if (medicine.isEmpty()) {
            throw new PharmaBusinessException(510, "Medicine code does not exist");
        }

        stopWatch.stop();
        logger.info("Time taken to check medicine code existence: {} ms", stopWatch.getTotalTimeMillis());
    }

    /**
     * Calculates the shipping charge for the given batch.
     * @param batchInfo The batch information for which to calculate the shipping charge.
     * @throws PharmaBusinessException if no shipping charge is found for the given medicine type code and weight range.
     */
    void calculateShippingCharge(BatchInfo batchInfo) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Calculate Shipping Charge");

        Optional<ShippingMaster> shippingMaster = shippingMasterRepository.findByMedicineTypeCodeAndWeightRange(batchInfo.getMedicineTypeCode(), getWeightRange(batchInfo.getWeight()));
        if (shippingMaster.isPresent()) {
            double shippingCharge = shippingMaster.get().getShippingCharge();
            if ("Yes".equals(batchInfo.getRefrigeration())) {
                shippingCharge += (shippingCharge * 0.05); // Add 5% for refrigeration
            }
            batchInfo.setShippingCharge(shippingCharge);
        } else {
            throw new PharmaBusinessException(514, "Shipping charge not found for the given medicine type code and weight range");
        }

        stopWatch.stop();
        logger.info("Time taken to calculate shipping charge: {} ms", stopWatch.getTotalTimeMillis());
    }

    /**
     * Determines the care level for the given batch based on the medicine type.
     * @param batchInfo The batch information for which to determine the care level.
     * @throws PharmaBusinessException if the medicine type is not found.
     */
    void calculateCareLevel(BatchInfo batchInfo) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Calculate Care Level");

        Optional<MedicalTypeMaster> medicalTypeMaster = medicalTypeMasterRepository.findById(batchInfo.getMedicineTypeCode());
        if (medicalTypeMaster.isPresent()) {
            String medicineTypeName = medicalTypeMaster.get().getMedicineTypeName();
            switch (medicineTypeName) {
                case "Capsules":
                    batchInfo.setCareLevel("Normal");
                    break;
                case "Tablets":
                    batchInfo.setCareLevel("High");
                    break;
                case "Syrups":
                    batchInfo.setCareLevel("Extremely High");
                    break;
                default:
                    batchInfo.setCareLevel("Normal");
                    break;
            }
        } else {
            throw new PharmaBusinessException(515, "Medicine type not found");
        }

        stopWatch.stop();
        logger.info("Time taken to calculate care level: {} ms", stopWatch.getTotalTimeMillis());
    }


    /**
     * Determines the weight range based on the weight of the batch.
     * @param weight The weight of the batch.
     * @return The weight range identifier.
     */
    private String getWeightRange(double weight) {
        if (weight <= 500) {
            return "W1";
        } else if (weight <= 1000) {
            return "W2";
        } else {
            return "W3";
        }
    }
}