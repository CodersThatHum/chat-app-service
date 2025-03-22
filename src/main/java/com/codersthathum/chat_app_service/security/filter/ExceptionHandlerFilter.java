package com.codersthathum.chat_app_service.security.filter;

import com.codersthathum.chat_app_service.dto.http.HttpResponse;
import com.codersthathum.chat_app_service.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException e) {
            handleException(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            handleException(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private void handleException(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");

        HttpResponse<Void> errorResponse = HttpResponse.error(status, message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
