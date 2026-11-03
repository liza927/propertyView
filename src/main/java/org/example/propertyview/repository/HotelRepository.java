package org.example.propertyview.repository;

import org.example.propertyview.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

    @Query("select new org.example.propertyview.repository.NameCount(h.brand, count(h)) from Hotel h group by h.brand")
    List<NameCount> countByBrand();

    @Query("select new org.example.propertyview.repository.NameCount(h.city, count(h)) from Hotel h group by h.city")
    List<NameCount> countByCity();

    @Query("select new org.example.propertyview.repository.NameCount(h.country, count(h)) from Hotel h group by h.country")
    List<NameCount> countByCountry();

    @Query("select new org.example.propertyview.repository.NameCount(a, count(h)) from Hotel h join h.amenities a group by a")
    List<NameCount> countByAmenity();
}