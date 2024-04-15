package com.ust.pharmaAssistant.controller;

import com.ust.pharmaAssistant.exception.PharmaBusinessException;
import com.ust.pharmaAssistant.model.BatchInfo;
import com.ust.pharmaAssistant.service.BatchInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling batch-related operations in the PharmaAssistant API.
 */
@RestController
@RequestMapping("/api/batch")
public class BatchInfoController {

    // Logger instance for logging
    private static final Logger logger = LoggerFactory.getLogger(BatchInfoController.class);

    // Autowired BatchInfoService for performing batch-related operations
    @Autowired
    private BatchInfoService batchInfoService;

    /**
     * Endpoint for adding a new batch.
     *
     * @param batchInfo BatchInfo object containing information about the batch.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
    @PostMapping("/add")
    public ResponseEntity<String> addBatch(@RequestBody BatchInfo batchInfo) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Add Batch in Controller");

        try {
            // Attempt to add the batch using the BatchInfoService
            boolean isAdded = batchInfoService.addBatch(batchInfo);
            stopWatch.stop();
            logger.info("Time taken to add batch in controller: {} ms", stopWatch.getTotalTimeMillis());

            // Get the calculated shipping charges and care level from the BatchInfo object
            double shippingCharge = batchInfo.getShippingCharge();
            String careLevel = batchInfo.getCareLevel();

            // Return success response with the calculated shipping charges and care level
            String responseMessage = String.format("Batch added successfully. Shipping Charge: %.2f, Care Level: %s", shippingCharge, careLevel);
            return ResponseEntity.ok(responseMessage);
        } catch (PharmaBusinessException e) {
            // Catch PharmaBusinessException and handle it by returning appropriate error response
            stopWatch.stop();
            logger.info("Time taken to add batch in controller: {} ms", stopWatch.getTotalTimeMillis());
            logger.error("Pharma Business Exception: {}", e.getMessage());
            return ResponseEntity.status(e.getErrorCode()).body(e.getMessage());
        } catch (Exception e) {
            // Catch any unexpected exceptions and handle them with a general error response
            stopWatch.stop();
            logger.info("Time taken to add batch in controller: {} ms", stopWatch.getTotalTimeMillis());
            logger.error("An unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("General system Error");
        }
    }
}