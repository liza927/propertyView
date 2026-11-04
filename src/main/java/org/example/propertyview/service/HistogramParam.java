package org.example.propertyview.service;

import org.example.propertyview.exception.InvalidParameterException;

import java.util.Arrays;

public enum HistogramParam {
    BRAND, CITY, COUNTRY, AMENITIES;

    public static HistogramParam from(String value) {
        return Arrays.stream(values())
                .filter(p -> p.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidParameterException(
                        "Unsupported histogram parameter '" + value + "'. Allowed: brand, city, country, amenities"));
    }
}