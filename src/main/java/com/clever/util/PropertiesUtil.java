package com.clever.util;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import static java.util.Collections.singletonMap;

public final class PropertiesUtil {

    public static Properties getProperties() {
        try (InputStream inputStream = PropertiesUtil.class
                .getClassLoader()
                .getResourceAsStream("application.yml")) {
            Yaml yaml = new Yaml();
            Map<String, Object> source = yaml.load(inputStream);
            Properties properties = new Properties();
            properties.putAll(getFlattenedMap(source));
            return properties;
        } catch (IOException e) {
            throw new Error("Cannot load properties", e);
        }
    }

    private static Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        buildFlattenedMap(result, source, null);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
        source.forEach((key, value) -> {
            if (path != null && !path.isEmpty())
                key = path + (key.startsWith("[") ? key : '.' + key);
            if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof Map) {
                buildFlattenedMap(result, (Map<String, Object>) value, key);
            } else if (value instanceof Collection) {
                int count = 0;
                for (Object object : (Collection<?>) value)
                    buildFlattenedMap(result, singletonMap("[" + (count++) + "]", object), key);
            } else {
                result.put(key, value != null ? "" + value : "");
            }
        });
    }
}
