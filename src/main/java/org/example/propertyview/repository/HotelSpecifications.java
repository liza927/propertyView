package org.example.propertyview.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.example.propertyview.entity.Hotel;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class HotelSpecifications {

    private HotelSpecifications() {
    }

    public static Specification<Hotel> all() {
        return (root, query, cb) -> cb.conjunction();
    }

    public static Specification<Hotel> equalsIgnoreCase(String field, String value) {
        if (value == null || value.isBlank()) {
            return all();
        }
        String v = value.trim().toLowerCase();
        return (root, query, cb) -> cb.equal(cb.lower(root.get(field)), v);
    }

    public static Specification<Hotel> nameContains(String value) {
        if (value == null || value.isBlank()) {
            return all();
        }
        String v = "%" + value.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), v);
    }

    public static Specification<Hotel> hasAllAmenities(List<String> amenities) {
        if (amenities == null || amenities.isEmpty()) {
            return all();
        }
        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            for (String amenity : amenities) {
                if (amenity == null || amenity.isBlank()) {
                    continue;
                }
                Join<Hotel, String> join = root.join("amenities");
                predicates.add(cb.equal(cb.lower(join), amenity.trim().toLowerCase()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}