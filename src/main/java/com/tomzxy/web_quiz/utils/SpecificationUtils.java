package com.tomzxy.web_quiz.utils;

import org.springframework.data.jpa.domain.Specification;

public class SpecificationUtils {

    public static <T> Specification<T> equal(String field, Object value) {
        return (root, query, cb) -> {
            if (value == null) return null;
            return cb.equal(root.get(field), value);
        };
    }

    public static <T> Specification<T> like(String field, String value) {
        return (root, query, cb) -> {
            if (value == null || value.isBlank()) return null;
            return cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
        };
    }

    public static <T> Specification<T> greaterThanOrEqual(String field, Number value) {
        return (root, query, cb) -> {
            if (value == null) return null;
            return cb.ge(root.get(field), value);
        };
    }

    public static <T> Specification<T> lessThanOrEqual(String field, Number value) {
        return (root, query, cb) -> {
            if (value == null) return null;
            return cb.le(root.get(field), value);
        };
    }
    public static <T> Specification<T> isTrue(String field) {
        return (root, query, cb) -> cb.isTrue(root.get(field));
    }
}