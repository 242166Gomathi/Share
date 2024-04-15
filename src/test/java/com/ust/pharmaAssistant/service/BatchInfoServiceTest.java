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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class BatchInfoServiceTest {

    @Mock
    private BatchInfoRepository batchInfoRepository;

    @Mock
    private MedicalTypeMasterRepository medicalTypeMasterRepository;

    @Mock
    private ShippingMasterRepository shippingMasterRepository;

    @Mock
    private MedicineRepository medicineRepository;
    @InjectMocks
    private BatchInfoService batchInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // Helper method to create a BatchInfo instance
    private BatchInfo createBatchInfo(String medicineTypeCode, int weight, String refrigeration) {
        BatchInfo batchInfo = new BatchInfo();
        batchInfo.setMedicineTypeCode(medicineTypeCode);
        batchInfo.setWeight(weight);
        batchInfo.setRefrigeration(refrigeration);
        return batchInfo;
    }

    @ParameterizedTest
    @CsvSource({
            "M1, 400, No, 10.0", // Shipping charge found
            "M1, 400, Yes, 10.5" // Shipping charge with refrigeration
    })
    void calculateShippingCharge_WhenShippingChargeFound(String medicineTypeCode, int weight, String refrigeration, double expectedCharge) {
        // Mocking data
        BatchInfo batchInfo = createBatchInfo(medicineTypeCode, weight, refrigeration);

        ShippingMaster shippingMaster = new ShippingMaster();
        shippingMaster.setShippingCharge(10); // Set a shipping charge

        when(shippingMasterRepository.findByMedicineTypeCodeAndWeightRange(medicineTypeCode, "W1"))
                .thenReturn(Optional.of(shippingMaster));

        // Test
        batchInfoService.calculateShippingCharge(batchInfo);

        // Verification
        assertEquals(expectedCharge, batchInfo.getShippingCharge(), 0.01); // Ensure correct shipping charge is set
    }

    @Test
    void calculateShippingCharge_WhenShippingChargeNotFound() {
        // Mocking data
        BatchInfo batchInfo = createBatchInfo("M1", 400, "No");

        when(shippingMasterRepository.findByMedicineTypeCodeAndWeightRange("M1", "W1"))
                .thenReturn(Optional.empty());

        // Test and verification using assertThrows
        PharmaBusinessException exception = assertThrows(PharmaBusinessException.class,
                () -> batchInfoService.calculateShippingCharge(batchInfo));

        assertEquals(514, exception.getErrorCode()); // Ensure correct error code
        assertEquals("Shipping charge not found for the given medicine type code and weight range", exception.getMessage()); // Ensure correct error message
    }

    @ParameterizedTest
    @CsvSource({
            "M1, Capsules, Normal", // Medicine type found (Capsules)
            "M2, Tablets, High", // Medicine type found (Tablets)
            "M3, Syrups, Extremely High" // Medicine type found (Syrups)
    })
    void calculateCareLevel_WhenMedicineTypeFound(String medicineTypeCode, String medicineTypeName, String expectedCareLevel) {
        // Mocking data
        BatchInfo batchInfo = createBatchInfo(medicineTypeCode, 0, "");

        MedicalTypeMaster medicalTypeMaster = new MedicalTypeMaster();
        medicalTypeMaster.setMedicineTypeName(medicineTypeName);

        when(medicalTypeMasterRepository.findById(medicineTypeCode))
                .thenReturn(Optional.of(medicalTypeMaster));

        // Test
        batchInfoService.calculateCareLevel(batchInfo);

        // Verification
        assertEquals(expectedCareLevel, batchInfo.getCareLevel()); // Ensure correct care level is set
    }

    @Test
    void calculateCareLevel_WhenMedicineTypeNotFound() {
        // Mocking data
        BatchInfo batchInfo = createBatchInfo("M1", 0, "");

        when(medicalTypeMasterRepository.findById("M1"))
                .thenReturn(Optional.empty());

        // Test and verification using assertThrows
        PharmaBusinessException exception = assertThrows(PharmaBusinessException.class,
                () -> batchInfoService.calculateCareLevel(batchInfo));

        assertEquals(515, exception.getErrorCode()); // Ensure correct error code
        assertEquals("Medicine type not found", exception.getMessage()); // Ensure correct error message
    }

    @Test
    void addBatch_EmptyMedicineCode_ShouldThrowException() {
        BatchInfo batchInfo = new BatchInfo();
        batchInfo.setBatchCode("BTC-1234");
        batchInfo.setWeight(200);
        batchInfo.setRefrigeration("No");

        PharmaBusinessException exception = assertThrows(PharmaBusinessException.class, () -> batchInfoService.addBatch(batchInfo));
        assertEquals(510, exception.getErrorCode());
        assertEquals("Medicine code is required", exception.getMessage());
    }

    @Test
    void addBatch_EmptyBatchCode_ShouldThrowException() {
        BatchInfo batchInfo = new BatchInfo();
        batchInfo.setMedicineCode("M123");
        batchInfo.setWeight(200);
        batchInfo.setRefrigeration("No");

        PharmaBusinessException exception = assertThrows(PharmaBusinessException.class, () -> batchInfoService.addBatch(batchInfo));
        assertEquals(513, exception.getErrorCode());
        assertEquals("Batch code is required", exception.getMessage());
    }

    @Test
    void addBatch_InvalidBatchCodeFormat_ShouldThrowException() {
        BatchInfo batchInfo = new BatchInfo();
        batchInfo.setMedicineCode("M123");
        batchInfo.setBatchCode("InvalidCode");
        batchInfo.setWeight(200);
        batchInfo.setRefrigeration("No");

        PharmaBusinessException exception = assertThrows(PharmaBusinessException.class, () -> batchInfoService.addBatch(batchInfo));
        assertEquals(513, exception.getErrorCode());
        assertEquals("Batch code should be in the format 'BTC-1234'", exception.getMessage());
    }

    @Test
    void addBatch_WeightLessThanMinimum_ShouldThrowException() {
        BatchInfo batchInfo = new BatchInfo();
        batchInfo.setMedicineCode("M123");
        batchInfo.setBatchCode("BTC-1234");
        batchInfo.setWeight(50); // Less than minimum allowed weight
        batchInfo.setRefrigeration("No");

        PharmaBusinessException exception = assertThrows(PharmaBusinessException.class, () -> batchInfoService.addBatch(batchInfo));
        assertEquals(512, exception.getErrorCode());
        assertEquals("Batch Weight should be greater than 100", exception.getMessage());
    }

    @Test
    void addBatch_WhenBatchCodeExists_ShouldThrowException() {
        // Mocking data
        BatchInfo batchInfo = new BatchInfo();
        batchInfo.setBatchCode("BTC-1234");

        // Mocking the behavior of batchInfoRepository.existsByBatchCode()
        when(batchInfoRepository.existsByBatchCode("BTC-1234"))
                .thenReturn(true);

        // Test and verification using assertThrows
        PharmaBusinessException exception = assertThrows(PharmaBusinessException.class,
                () -> batchInfoService.addBatch(batchInfo));

        // Verification
        // Ensure correct error code and message
        assertEquals(510, exception.getErrorCode());
        assertEquals("Medicine code is required", exception.getMessage());
    }
    @Test
    void testCheckForDuplicates_DuplicateBatchCode() {
        // Arrange
        BatchInfo batchInfo = new BatchInfo();
        batchInfo.setBatchCode("BTC-1234");

        Mockito.when(batchInfoRepository.existsByBatchCode("BTC-1234")).thenReturn(true);

        // Act and Assert
        Assertions.assertThrows(PharmaBusinessException.class, () -> batchInfoService.checkForDuplicates(batchInfo));
    }

    @Test
    void testCheckForDuplicates_UniqueBatchCode() {
        // Arrange
        BatchInfo batchInfo = new BatchInfo();
        batchInfo.setBatchCode("BTC-5678");

        Mockito.when(batchInfoRepository.existsByBatchCode("BTC-5678")).thenReturn(false);

        // Act
        batchInfoService.checkForDuplicates(batchInfo);

    }

    @Test
    void testCheckIfMedicineCodeExists_MedicineCodeExists() {
        // Arrange
        String medicineCode = "MED001";
        Mockito.when(medicineRepository.findById(medicineCode)).thenReturn(Optional.of(new Medicine()));

        // Act
        batchInfoService.checkIfMedicineCodeExists(medicineCode);

        // Assert (no exception should be thrown)
    }

    @Test
    void testCheckIfMedicineCodeExists_MedicineCodeNotFound() {
        // Arrange
        String medicineCode = "MED001";
        Mockito.when(medicineRepository.findById(medicineCode)).thenReturn(Optional.empty());

        // Act and Assert
        Assertions.assertThrows(PharmaBusinessException.class, () -> batchInfoService.checkIfMedicineCodeExists(medicineCode));
    }
}
