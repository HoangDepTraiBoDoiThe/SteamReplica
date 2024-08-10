package com.example.steamreplica.util;

import io.jsonwebtoken.io.IOException;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

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
}
