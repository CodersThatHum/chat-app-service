package com.codersthathum.chat_app_service.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Component
@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("startTime", String.valueOf(System.currentTimeMillis()));

        log.info(
                "httpClient Sent Request: uri={}, method={}",
                request.getRequestURI(),
                request.getMethod()
        );

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (response.getStatus() < 300) {
            log.info(
                    "httpClient Received Response: uri={}, method={}, status={}",
                    request.getRequestURI(),
                    request.getMethod(),
                    response.getStatus()
            );
        } else {
            log.error(
                    "httpClient Received Error Response: uri={}, method={}, status={}",
                    request.getRequestURI(),
                    request.getMethod(),
                    response.getStatus()
            );
        }
    }
}
