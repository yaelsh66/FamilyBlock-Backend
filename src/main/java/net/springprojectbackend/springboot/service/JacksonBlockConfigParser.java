package net.springprojectbackend.springboot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
class JacksonBlockConfigParser implements BlockConfigParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<String> parseJsonArray(String jsonArrayOrNull) {
        // If the DB field is null or empty, treat as an empty list.
        if (jsonArrayOrNull == null || jsonArrayOrNull.isBlank()) {
            return List.of();
        }

        try {
            // Parse JSON array into Java List<String>
            return objectMapper.readValue(jsonArrayOrNull, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            // If JSON is corrupted, we fail gracefully (empty list) to avoid
            // breaking the agent heartbeat.
            // You can log this later if you want.
            return List.of();
        }
    }
}
