package com.Toyota.gateway.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Builder
@AllArgsConstructor
@Data
@Component
public class GenericResponse<T> {

    private boolean success;
    private String message;
    private T data;

    @Autowired
    private MessageSource messageSource;

    public GenericResponse() {}

    public static <T> GenericResponse<T> success(MessageSource messageSource, String messageKey){
        return GenericResponse.<T>builder()
                .message(getMessage(messageSource, messageKey))
                .success(true)
                .build();
    }

    public static <T> GenericResponse<T> successResult(MessageSource messageSource, T data, String messageKey){
        return GenericResponse.<T>builder()
                .message(getMessage(messageSource, messageKey))
                .data(data)
                .success(true)
                .build();
    }

    public static <T> GenericResponse<T> errorResult(MessageSource messageSource, String messageKey){
        return GenericResponse.<T>builder()
                .message(getMessage(messageSource, messageKey))
                .success(false)
                .build();
    }

    private static String getMessage(MessageSource messageSource, String messageKey){
        return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }
}
