package com.sheerid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Standardize how we utilize Jackson for JSON serialization and deserialization, and provide convenience methods.
 *
 * The Jackson ObjectMapper is expensive to create. It is also thread-safe, so it is more efficient for many threads/consumers to share
 * a single instance.  However, because the configuration of the ObjectMapper can be changed by any consumer, and this could
 * potentially interfere with the behavior of other consumers, we cannot share a single ObjectMapper instance directly. We therefore
 * provide methods for obtaining an ObjectReader or an ObjectWriter from the ObjectMapper, which are very inexpensive to create, but
 * are also configurable in the same ways that the ObjectMapper is. This allows the consumer to efficiently create serializers and
 * deserializers while still attaining maximum flexibility and control.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * All methods are static, so no instance is ever needed.
     */
    private JsonUtils() {
    }

    public static ObjectReader getObjectReader() {
        return objectMapper.reader();
    }

    public static <T> T readValue(InputStream stream, Class<T> type) throws IOException {
        return objectMapper.readValue(stream, type);
    }

    public static <T> T readValue(InputStream stream, TypeReference typeRef) throws IOException {
        return objectMapper.readValue(stream, typeRef);
    }

    public static <T> T readValue(String value, Class<T> type) throws IOException {
        return objectMapper.readValue(value, type);
    }

    public static <T> T readValue(String value, TypeReference typeRef) throws IOException {
        return objectMapper.readValue(value, typeRef);
    }

    public static <T> T convertValue(Object value, Class<T> convertType) {
        return objectMapper.convertValue(value, convertType);
    }

    public static ObjectWriter getObjectWriter() {
        return objectMapper.writer();
    }

    public static String writeValueAsString(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    public static String writeValueAsJsonFormattedString(Object value) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter()
                           .writeValueAsString(value);
    }

    /**
     * Parse the given string as an object of the given class.
     *
     * @param value
     * @param type
     * @param <T>
     *
     * @return An Optional containing the parsed object if successful, or an empty Optional otherwise.
     */
    public static <T> Optional<T> parseSafely(String value, Class<T> type) {
        if (StringUtils.isBlank(value)) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(value, type));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Serialize the given object to a JSON string.
     *
     * @param value
     * @param defaultValue
     *
     * @return The serialized object, or the given defaultValue if the given object is null or an error occurred during serialization.
     */
    public static String serializeSafely(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return JsonUtils.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return defaultValue;
        }
    }

    /**
     * Serialize the given object to a JSON string, formatted to be easier to read.
     *
     * @param value
     * @param defaultValue
     *
     * @return The serialized object, or the given defaultValue if the given object is null or an error occurred during serialization.
     */
    public static String serializeFormattedSafely(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return JsonUtils.writeValueAsJsonFormattedString(value);
        } catch (JsonProcessingException e) {
            return defaultValue;
        }
    }

}
