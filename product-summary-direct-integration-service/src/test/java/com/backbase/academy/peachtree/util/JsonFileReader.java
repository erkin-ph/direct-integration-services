package com.backbase.academy.peachtree.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonFileReader {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonFileReader() {}

    public static <T> T readFromFile(String filePath, TypeReference<T> outputTypeReference) {
        T response = null;
        try {
            response = mapper.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath), outputTypeReference);
        } catch (IOException e) {
            log.info("Failed to read content from file " + filePath, e);
        }
        return response;
    }

    public static <T> T readFromFile(String filePath, Class<T> outputClass) {
        T response = null;
        try {
            response = mapper.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath), outputClass);
        } catch (IOException e) {
            log.info("Failed to read content from file " + filePath, e);
        }
        return response;
    }

}
