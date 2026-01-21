package com.courier;

import com.courier.infra.CorrelationIdFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CorrelationIdFilterTest {

    @Test
    void generatesAndPropagatesCorrelationId() throws ServletException, IOException {
        CorrelationIdFilter filter = new CorrelationIdFilter();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        when(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).setHeader(Mockito.eq(CorrelationIdFilter.CORRELATION_ID_HEADER), Mockito.anyString());
        verify(chain).doFilter(request, response);
        assertNull(MDC.get(CorrelationIdFilter.CORRELATION_ID_HEADER));
    }

    @Test
    void respectsExistingCorrelationId() throws ServletException, IOException {
        CorrelationIdFilter filter = new CorrelationIdFilter();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        String incoming = UUID.randomUUID().toString();
        when(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn(incoming);

        filter.doFilter(request, response, chain);

        verify(response).setHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, incoming);
        verify(chain).doFilter(request, response);
        assertNull(MDC.get(CorrelationIdFilter.CORRELATION_ID_HEADER));
    }

    @Test
    void generatesWhenHeaderBlank() throws ServletException, IOException {
        CorrelationIdFilter filter = new CorrelationIdFilter();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        when(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn("   ");

        filter.doFilter(request, response, chain);

        verify(response).setHeader(Mockito.eq(CorrelationIdFilter.CORRELATION_ID_HEADER), Mockito.anyString());
        verify(chain).doFilter(request, response);
        assertNull(MDC.get(CorrelationIdFilter.CORRELATION_ID_HEADER));
    }
}

