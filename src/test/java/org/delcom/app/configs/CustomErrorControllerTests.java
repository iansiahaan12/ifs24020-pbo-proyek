package org.delcom.app.configs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; 
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class CustomErrorControllerTest {

    // GANTI @MockBean MENJADI @MockitoBean
    @MockitoBean
    private ErrorAttributes errorAttributes;

    @Autowired
    private CustomErrorController controller;

    @Test
    @DisplayName("Mengembalikan response error dengan status 500")
    void testHandleErrorReturns500() {
        // Arrange
        Map<String, Object> errorMap = Map.of(); 

        when(errorAttributes.getErrorAttributes(
                any(ServletWebRequest.class),
                any(ErrorAttributeOptions.class)))
                .thenReturn(errorMap);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletWebRequest webRequest = new ServletWebRequest(request, response);

        // Act
        ResponseEntity<Map<String, Object>> result = controller.handleError(webRequest);

        // Assert
        assertEquals(500, result.getStatusCode().value());
        assertEquals("error", result.getBody().get("status"));
        assertEquals("Unknown Error", result.getBody().get("error"));
        assertEquals("Endpoint tidak ditemukan atau terjadi error", result.getBody().get("message"));
    }

    @Test
    @DisplayName("Mengembalikan response error dengan status 404")
    void testHandleErrorReturns404() {
        // Arrange
        Map<String, Object> errorMap = Map.of(
                "status", 404,
                "error", "Not Found",
                "path", "/error404");

        when(errorAttributes.getErrorAttributes(
                any(ServletWebRequest.class),
                any(ErrorAttributeOptions.class)))
                .thenReturn(errorMap);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletWebRequest webRequest = new ServletWebRequest(request, response);

        // Act
        ResponseEntity<Map<String, Object>> result = controller.handleError(webRequest);

        // Assert
        assertEquals(404, result.getStatusCode().value());
        assertEquals("fail", result.getBody().get("status"));
        assertEquals("Not Found", result.getBody().get("error"));
        assertEquals("/error404", result.getBody().get("path"));
    }
}