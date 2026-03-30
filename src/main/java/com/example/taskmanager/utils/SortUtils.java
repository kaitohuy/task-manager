package com.example.taskmanager.utils;

import com.example.taskmanager.config.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

public class SortUtils {
    public static Pageable mapSort(Pageable pageable, Map<String, String> sortMapping) {

        if (pageable.getSort().isUnsorted()) {
            return pageable;
        }

        List<Sort.Order> orders = pageable.getSort().stream()
                .map(order -> {
                    String property = order.getProperty();

                    if (!sortMapping.containsKey(property)) {
                        throw new BadRequestException("Invalid sort field: " + property);
                    }

                    return new Sort.Order(
                            order.getDirection(),
                            sortMapping.get(property)
                    );
                })
                .toList();

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(orders)
        );
    }
}
