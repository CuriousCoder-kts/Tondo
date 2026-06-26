package com.tondo.common.util;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagUtilTest {

    @Test
    void overlapScore_fullMatch() {
        Set<String> a = Set.of("职业迷茫", "情感困扰");
        Set<String> b = Set.of("职业迷茫", "情感困扰");
        assertEquals(1.0, TagUtil.overlapScore(a, b));
    }

    @Test
    void overlapScore_partialMatch() {
        Set<String> a = Set.of("职业迷茫", "情感困扰", "学业压力");
        Set<String> b = Set.of("职业迷茫");
        assertEquals(0.333, Math.round(TagUtil.overlapScore(a, b) * 1000) / 1000.0);
    }

    @Test
    void overlapScore_emptyReturnsZero() {
        assertEquals(0.0, TagUtil.overlapScore(Set.of(), Set.of("a")));
    }

    @Test
    void parseTags_fromJson() {
        assertEquals(2, TagUtil.parseTags("[\"焦虑\",\"迷茫\"]").size());
    }
}
