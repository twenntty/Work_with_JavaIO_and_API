package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;

public class ApiService {

    private static final String API_URL = "http://api.recrm.ru";
    private static final String FORMAT = "json";
    private static final String API_KEY = "43a0cef4dd3b4c818a5328154582ef5d";
    private static final String EXCEL_FILE_NAME = "output.xlsx";

    public Map<String, Object> fetchDataFromApi() {
        Map<String, Object> apiResponse = new HashMap<>();

        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            String endpointPath = String.format("/%s/countries?key=%s", FORMAT, API_KEY);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + endpointPath))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                apiResponse = parseJson(response.body());
            } else {
                System.out.println("API request failed with status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apiResponse;
    }

    private Map<String, Object> parseJson(String jsonData) throws Exception {
        Map<String, Object> parsedData = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(jsonData);
        JsonNode countriesArray = rootNode.get("countries");

        if (countriesArray != null && countriesArray.isArray()) {
            for (JsonNode countryNode : countriesArray) {
                JsonNode idNode = countryNode.get("id");
                JsonNode nameNode = countryNode.get("name");

                if (idNode != null && nameNode != null) {
                    parsedData.put(idNode.asText(), nameNode.asText());
                } else {
                    System.out.println("Skipping entry due to missing id or name");
                }
            }
        } else {
            System.out.println("No 'countries' array found in the JSON response");
        }

        return parsedData;
    }

     void createExcelFile(Map<String, Object> data) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("API Data");
            int rowNum = 0;

            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0, CellType.STRING).setCellValue("ID");
            headerRow.createCell(1, CellType.STRING).setCellValue("Name");

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0, CellType.STRING).setCellValue(entry.getKey());
                row.createCell(1, CellType.STRING).setCellValue(entry.getValue().toString());
            }

            try (FileOutputStream fileOut = new FileOutputStream(EXCEL_FILE_NAME)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fetchDataAndGenerateExcel() {
        Map<String, Object> dataFromApi = fetchDataFromApi();
        createExcelFile(dataFromApi);
    }

    public static void main(String[] args) {
        ApiService apiService = new ApiService();
        apiService.fetchDataAndGenerateExcel();
    }
}
