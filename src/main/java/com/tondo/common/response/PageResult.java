package com.tondo.common.response;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private long page;
    private long size;
    private long total;
    private List<T> records;

    public static <T> PageResult<T> of(long page, long size, long total, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.page = page;
        result.size = size;
        result.total = total;
        result.records = records;
        return result;
    }
}
