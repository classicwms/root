package com.tekclover.wms.api.transaction.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** SQL Server allows max 2100 parameters per statement; keep IN lists well below that. */
public final class Chunks {
    private Chunks() {
    }

    public static <T> List<List<T>> of(List<T> source, int size) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<T>> out = new ArrayList<>((source.size() + size - 1) / size);
        for (int i = 0; i < source.size(); i += size) {
            out.add(new ArrayList<>(source.subList(i, Math.min(i + size, source.size()))));
        }
        return out;
    }
}