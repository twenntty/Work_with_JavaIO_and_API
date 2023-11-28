package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Map;

public class ApiServiceTest {

    @Test
    void testFetchDataFromApi() {
        ApiService apiService = new ApiService();
        Map<String, Object> data = apiService.fetchDataFromApi();
        Assertions.assertNotNull(data);
        Assertions.assertFalse(data.isEmpty());
    }

    @Test
    void testCreateExcelFile() {
        ApiService apiService = new ApiService();
        Map<String, Object> testData = Map.of("1", "Test Country 1", "2", "Test Country 2");
        apiService.createExcelFile(testData);
    }
}
