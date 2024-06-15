package com.Toyota.product.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Builder
@AllArgsConstructor
@Data
public class GenericResponse<T> {
    private static MessageSource messageSource;
    private boolean success;
    private String message;
    private T data;

    public static void setMessageSource(MessageSource messageSource){

        GenericResponse.messageSource=messageSource;
    }
    public static <T> GenericResponse<T> success(String messageKey){
        return GenericResponse.<T>builder()
                .message(getMessage(messageKey))
                .success(true)
                .build();
    }
    public static <T> GenericResponse<T>successResult(T data , String messageKey){
        return GenericResponse.<T>builder()
                .message(getMessage(messageKey))
                .data(data)
                .success(true)
                .build();
    }
    public static <T> GenericResponse<T>errorResult(String messageKey){
        return GenericResponse.<T>builder()
                .message(getMessage(messageKey))
                .success(false)
                .build();
    }

    private static String getMessage(String messageKey){
        return messageSource.getMessage(messageKey,null, LocaleContextHolder.getLocale());
    }

}
