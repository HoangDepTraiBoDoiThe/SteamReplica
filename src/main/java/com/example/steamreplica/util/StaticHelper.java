package com.example.steamreplica.util;

import io.jsonwebtoken.io.IOException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StaticHelper {
    public static String convertBlobToString(Blob roomPic) {
        if (roomPic == null) {
            return null;
        }
        try {
            byte[] roomPicBytes = roomPic.getBytes(1, (int) roomPic.length());
            return Base64.getEncoder().encodeToString(roomPicBytes);
        } catch (SQLException e) {
            throw new RuntimeException("Can't convert blob to string: " + e);
        }
    }

    public static Blob convertToBlob(String image) {
        if (image != null && !image.isEmpty()) {
            try {
                byte[] multiPartFileBytes = image.getBytes();
                return new SerialBlob(multiPartFileBytes);
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static Map<String, String> extractBindingErrorMessages(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }
    public static <R> R catchingBindingError(BindingResult result, Function<List<String>, R> bindingResultExceptionFunction) {
        List<String> errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        return bindingResultExceptionFunction.apply(errors);
    }

    public static Collection<String> extractGrantedAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }
}
