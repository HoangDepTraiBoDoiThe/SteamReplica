package com.example.steamreplica.util;

import io.jsonwebtoken.io.IOException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

    public static List<String> extractBindingErrorMessages(BindingResult result) {
        if (result.hasErrors()) {
            return result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        }
        return new ArrayList<>();
    }
}
