package com.tondo.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TagUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TagUtil() {
    }

    public static List<String> parseTags(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            List<String> tags = MAPPER.readValue(json, new TypeReference<>() {});
            return tags != null ? tags : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static Set<String> parseTagSet(String json) {
        return new HashSet<>(parseTags(json));
    }

    public static double overlapScore(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }
        long shared = a.stream().filter(b::contains).count();
        return (double) shared / Math.max(a.size(), b.size());
    }
}
