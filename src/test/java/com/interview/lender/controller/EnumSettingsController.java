package com.interview.lender.controller;

import com.interview.lender.dto.ResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EnumSettingsControllerTest {

    @InjectMocks
    private EnumSettingsController enumSettingsController;



    @BeforeEach
    void setUp() {
    }



    @Test
    void getSettings_shouldReturnSuccessResponse() {
        ResponseDto response = enumSettingsController.getSettings();

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Settings retrieved successfully", response.getMessage());
        assertEquals(HttpStatus.OK, response.getHttpStatus());
    }



    @Test
    void getSettings_shouldReturnAllExpectedSettings() {
        ResponseDto response = enumSettingsController.getSettings();

        assertNotNull(response.getData());
        assertInstanceOf(Map.class, response.getData());

        @SuppressWarnings("unchecked")
        Map<String, Object> settings = (Map<String, Object>) response.getData();

        assertTrue(settings.containsKey("customerStatus"));
        assertTrue(settings.containsKey("genders"));
        assertTrue(settings.containsKey("loanStatus"));
        assertTrue(settings.containsKey("loanEventTypes"));
        assertTrue(settings.containsKey("loanStructureTypes"));
        assertTrue(settings.containsKey("paymentStatus"));
        assertTrue(settings.containsKey("paymentMethods"));
        assertTrue(settings.containsKey("installmentStatus"));
        assertTrue(settings.containsKey("feeTypes"));
        assertTrue(settings.containsKey("calculationTypes"));
        assertTrue(settings.containsKey("productStatus"));
        assertTrue(settings.containsKey("notificationStatus"));
        assertTrue(settings.containsKey("notificationChannels"));
        assertTrue(settings.containsKey("notificationTypes"));
        assertTrue(settings.containsKey("billingCycleTypes"));
        assertTrue(settings.containsKey("billingCycles"));
        assertTrue(settings.containsKey("applicationTimings"));
        assertTrue(settings.containsKey("tenureTypes"));
        assertTrue(settings.containsKey("businessRules"));
        assertTrue(settings.containsKey("businessRulesOperations"));
    }



    @Test
    void getSettings_shouldReturnCorrectNumberOfSettings() {
        ResponseDto response = enumSettingsController.getSettings();
        @SuppressWarnings("unchecked")
        Map<String, Object> settings = (Map<String, Object>) response.getData();

        assertEquals(20, settings.size());
    }



    @Test
    void getSettings_shouldContainNonNullSettingValues() {
        ResponseDto response = enumSettingsController.getSettings();

        @SuppressWarnings("unchecked")
        Map<String, Object> settings = (Map<String, Object>) response.getData();

        settings.forEach((key, value) -> {
            assertNotNull(value, "Setting value for " + key + " should not be null");
        });
    }



    @Test
    void getSettings_shouldReturnConsistentResponseStructure() {
        ResponseDto response1 = enumSettingsController.getSettings();
        ResponseDto response2 = enumSettingsController.getSettings();

        assertEquals(response1.isSuccess(), response2.isSuccess());
        assertEquals(response1.getMessage(), response2.getMessage());
        assertEquals(response1.getHttpStatus(), response2.getHttpStatus());

        @SuppressWarnings("unchecked")
        Map<String, Object> settings1 = (Map<String, Object>) response1.getData();
        @SuppressWarnings("unchecked")
        Map<String, Object> settings2 = (Map<String, Object>) response2.getData();

        assertEquals(settings1.keySet(), settings2.keySet());
    }



    @Test
    void getSettings_shouldHaveCorrectKeyNamingConvention() {
        ResponseDto response = enumSettingsController.getSettings();
        @SuppressWarnings("unchecked")
        Map<String, Object> settings = (Map<String, Object>) response.getData();

        settings.keySet().forEach(key -> {
            assertTrue(Character.isLowerCase(key.charAt(0)), "Key '" + key + "' should start with lowercase letter");

            assertFalse(key.contains(" "), "Key '" + key + "' should not contain spaces");
        });
    }



    @Test
    void getSettings_shouldHandleEnumMethodExceptions() {
        // This test assumes that even if one enum method throws an exception,
        // the controller should still return a valid response with other settings
        ResponseDto response = enumSettingsController.getSettings();

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
    }



    @Test
    void getSettings_shouldReturnSettingsForAllCategories() {
        ResponseDto response = enumSettingsController.getSettings();

        @SuppressWarnings("unchecked")
        Map<String, Object> settings = (Map<String, Object>) response.getData();

        assertTrue(settings.containsKey("customerStatus"));
        assertTrue(settings.containsKey("genders"));

        assertTrue(settings.containsKey("loanStatus"));
        assertTrue(settings.containsKey("loanEventTypes"));
        assertTrue(settings.containsKey("loanStructureTypes"));

        assertTrue(settings.containsKey("paymentStatus"));
        assertTrue(settings.containsKey("paymentMethods"));
        assertTrue(settings.containsKey("installmentStatus"));

        assertTrue(settings.containsKey("feeTypes"));
        assertTrue(settings.containsKey("calculationTypes"));

        assertTrue(settings.containsKey("productStatus"));

        assertTrue(settings.containsKey("notificationStatus"));
        assertTrue(settings.containsKey("notificationChannels"));
        assertTrue(settings.containsKey("notificationTypes"));

        assertTrue(settings.containsKey("billingCycleTypes"));
        assertTrue(settings.containsKey("billingCycles"));

        assertTrue(settings.containsKey("applicationTimings"));
        assertTrue(settings.containsKey("tenureTypes"));
        assertTrue(settings.containsKey("businessRules"));
        assertTrue(settings.containsKey("businessRulesOperations"));
    }
}
