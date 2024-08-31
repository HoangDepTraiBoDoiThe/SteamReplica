package com.example.steamreplica.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.jsonwebtoken.io.IOException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class BlobDeserializer extends JsonDeserializer<Blob> {
    @Override
    public Blob deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, java.io.IOException {
        // Assuming the Blob is serialized as a base64 encoded string
        String base64Data = p.getValueAsString();
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(base64Data);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            return new javax.sql.rowset.serial.SerialBlob(bytes);
        } catch (SQLException e) {
            throw new IOException("Failed to create Blob instance", e);
        }
    }
}