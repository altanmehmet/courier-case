package com.courier;

import com.courier.api.ApiError;
import com.courier.api.ApiExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiExceptionHandlerTest {

    @Test
    void handleValidationFormatsFieldErrorsAndReturns400() throws Exception {
        ApiExceptionHandler handler = new ApiExceptionHandler();
        HttpServletRequest request = new MockHttpServletRequest("POST", "/locations");

        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "locationRequest");
        bindingResult.addError(new FieldError("locationRequest", "courierId", "must not be blank"));
        bindingResult.addError(new FieldError("locationRequest", "timeMillis", "must be greater than or equal to 1"));

        Method method = DummyController.class.getDeclaredMethod("dummy", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ApiError> response = handler.handleValidation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("/locations", response.getBody().path());
        assertTrue(response.getBody().message().contains("courierId must not be blank"));
        assertTrue(response.getBody().message().contains("timeMillis must be greater than or equal to 1"));
    }

    @Test
    void handleConstraintViolationReturns400() {
        ApiExceptionHandler handler = new ApiExceptionHandler();
        HttpServletRequest request = new MockHttpServletRequest("GET", "/entries");

        ConstraintViolationException ex = new ConstraintViolationException("bad param", Set.of());
        ResponseEntity<ApiError> response = handler.handleConstraintViolation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("bad param", response.getBody().message());
        assertEquals("/entries", response.getBody().path());
    }

    @Test
    void handleBadJsonReturns400WithFixedMessage() {
        ApiExceptionHandler handler = new ApiExceptionHandler();
        HttpServletRequest request = new MockHttpServletRequest("POST", "/locations");

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("cannot parse", (HttpMessageConversionException) null, null);
        ResponseEntity<ApiError> response = handler.handleBadJson(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Malformed JSON request", response.getBody().message());
    }

    @Test
    void handleGenericReturns500() {
        ApiExceptionHandler handler = new ApiExceptionHandler();
        HttpServletRequest request = new MockHttpServletRequest("GET", "/couriers/c1/distance");

        RuntimeException ex = new RuntimeException("boom");
        ResponseEntity<ApiError> response = handler.handleGeneric(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("boom", response.getBody().message());
    }

    static class DummyController {
        @SuppressWarnings("unused")
        void dummy(String ignored) {
        }
    }
}

