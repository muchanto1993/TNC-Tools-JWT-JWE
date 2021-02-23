package com.tnc.app.support;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {
    
    public static Object convertStringToObject(String input) {
        try {
            return new ObjectMapper().readValue(input, Object.class);
        } catch (Exception e) {
            log.error("Err", e);
            return null;
        }
    }

    public static List<Object> convertStringToObjectList(String input) {
        try {
            return new ObjectMapper().readValue(input, new TypeReference<List<Object>>() {
            });
        } catch (Exception e) {
            log.error("Err", e);
            return null;
        }
    }

    public static String objectToJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Err", e);
            return null;
        }
    }

    public static String objectToJsonPrettyString(Object obj) {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Err", e);
            return null;
        }
    }

}
