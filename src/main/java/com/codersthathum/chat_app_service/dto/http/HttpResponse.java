package com.codersthathum.chat_app_service.dto.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public  class HttpResponse<T> {

    private Integer statusCode;

    private HttpStatus status;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

   public static <T> HttpResponse<T> success(HttpStatus status, String message, T data) {
       return HttpResponse.<T>builder()
               .statusCode(status.value())
               .status(status)
               .message(message)
               .data(data)
               .build();
   }

   public static <T> HttpResponse<T> success(HttpStatus status, String message) {
       return HttpResponse.<T>builder()
               .statusCode(status.value())
               .status(status)
               .message(message)
               .build();
   }

   public static <T> HttpResponse<T> error(HttpStatus status, String message) {
       return HttpResponse.<T>builder()
               .statusCode(status.value())
               .status(status)
               .message("Error")
               .error(message)
               .build();
   }

}
