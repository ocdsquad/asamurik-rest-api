package com.asamurik_rest_api.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TransformPagination {
    public Map<String, Object> transformPagination(
            List<?> contentList,
            Page<?> page,
            String columnName,
            String filterValue
    ) {
        Sort sort = page.getSort();
        String sortByColumn = "id";
        String sortDirection = "asc";

        if (sort != null && !sort.isUnsorted()) {
            String[] sortArr = sort.toString().split(":");
            if (sortArr.length == 2) {
                sortByColumn = sortArr[0].trim();
                sortDirection = sortArr[1].trim().toLowerCase();
            }
        }

        Map<String, Object> paginationData = new HashMap<>();
        paginationData.put("content", contentList);
        paginationData.put("total-data", page.getTotalElements());
        paginationData.put("total-pages", page.getTotalPages());
        paginationData.put("current-page", page.getNumber());
        paginationData.put("size-per-page", page.getSize());
        paginationData.put("sort-by", sortByColumn);
        paginationData.put("sort", sortDirection);
        paginationData.put("column-name", columnName);
        paginationData.put("value", filterValue == null ? "" : filterValue);

        return paginationData;
    }
}
