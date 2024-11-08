package com.sequenceiq.externalizedcompute.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cloudera.thunderhead.service.liftiepublic.LiftiePublicProto;
import com.sequenceiq.cloudbreak.common.exception.CloudbreakServiceException;

@ExtendWith(MockitoExtension.class)
class LiftieValidationResponseUtilTest {

    @InjectMocks
    private LiftieValidationResponseUtil underTest;

    @Test
    public void testWhenValidationContainsFailedValidation() {
        LiftiePublicProto.ValidationResponse validationResponse = LiftiePublicProto.ValidationResponse.newBuilder()
                .addValidations(LiftiePublicProto.ValidationResult.newBuilder()
                        .setStatus("FAILED")
                        .setMessage("Failed 1 message.")
                        .setDetailedMessage("Detailed message 1.")
                        .build())
                .addValidations(LiftiePublicProto.ValidationResult.newBuilder()
                        .setStatus("SKIPPED")
                        .setDetailedMessage("Skipped")
                        .build())
                .addValidations(LiftiePublicProto.ValidationResult.newBuilder()
                        .setStatus("FAILED")
                        .setMessage("Failed 2 message.")
                        .setDetailedMessage("Detailed message 2.")
                        .build())
                .build();

        CloudbreakServiceException exception = assertThrows(CloudbreakServiceException.class,
                () -> underTest.throwException(validationResponse));

        assertEquals("Validation failed: Error: Failed 1 message. Reason: Detailed message 1. Error: Failed 2 message. Reason: Detailed message 2.",
                exception.getMessage());
    }

    @Test
    public void testWhenValidationDoesNotContainFailedValidation() {
        LiftiePublicProto.ValidationResponse validationResponse = LiftiePublicProto.ValidationResponse.newBuilder()
                .addValidations(LiftiePublicProto.ValidationResult.newBuilder()
                        .setStatus("SUCCESS")
                        .setMessage("Success 1.")
                        .setDetailedMessage("Detailed message 1.")
                        .build())
                .addValidations(LiftiePublicProto.ValidationResult.newBuilder()
                        .setStatus("SKIPPED")
                        .setDetailedMessage("Detailed message 2.")
                        .build())
                .addValidations(LiftiePublicProto.ValidationResult.newBuilder()
                        .setStatus("SKIPPED")
                        .setMessage("Skipped 1.")
                        .setDetailedMessage("Detailed message 3.")
                        .build())
                .build();

        assertDoesNotThrow(() -> underTest.throwException(validationResponse));
    }

    @Test
    public void testWhenValidationResponseIsNull() {
        assertDoesNotThrow(() -> underTest.throwException(null));
    }
}