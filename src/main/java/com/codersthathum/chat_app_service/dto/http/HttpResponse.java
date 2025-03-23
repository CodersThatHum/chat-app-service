package com.codersthathum.chat_app_service.dto.http;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HttpResponse<T> {

    private String message;

    private Meta meta;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

    public static <T> HttpResponse<T> success(HttpServletRequest request, HttpStatus status, String message, T data) {
        return HttpResponse.<T>builder()
                .message(message)
                .meta(
                        Meta.builder()
                                .requestId(MDC.get("requestId"))
                                .path(request.getRequestURI())
                                .timestamp(System.currentTimeMillis())
                                .timeElapsed(getTimeElapsed())
                                .statusCode(status.value())
                                .status(status)
                                .build()
                )
                .data(data)
                .build();
    }

    public static <T> HttpResponse<T> success(HttpServletRequest request, HttpStatus status, String message) {
        return HttpResponse.<T>builder()
                .message(message)
                .meta(
                        Meta.builder()
                                .requestId(MDC.get("requestId"))
                                .path(request.getRequestURI())
                                .timestamp(System.currentTimeMillis())
                                .timeElapsed(getTimeElapsed())
                                .statusCode(status.value())
                                .status(status)
                                .build()
                )
                .build();
    }

    public static <T> HttpResponse<T> error(HttpServletRequest request, HttpStatus status, String message) {
        return HttpResponse.<T>builder()
                .message("Error")
                .meta(
                        Meta.builder()
                                .requestId(MDC.get("requestId"))
                                .path(request.getRequestURI())
                                .timestamp(System.currentTimeMillis())
                                .timeElapsed(getTimeElapsed())
                                .statusCode(status.value())
                                .status(status)
                                .build()
                )
                .error(message)
                .build();
    }

    private static String getTimeElapsed() {
        String startTimeString = MDC.get("startTime");
        if (startTimeString == null) {
            return null;
        }

        return System.currentTimeMillis() - Long.parseLong(startTimeString) + "ms";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class Meta {

        private String requestId;

        private String path;

        private Integer statusCode;

        private HttpStatus status;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        private Long timestamp;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String timeElapsed;

    }

}
