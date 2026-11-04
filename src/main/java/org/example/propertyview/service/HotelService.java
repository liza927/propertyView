package org.example.propertyview.service;

import org.example.propertyview.dto.HotelCreateRequest;
import org.example.propertyview.dto.HotelDetailsDto;
import org.example.propertyview.dto.HotelSummaryDto;
import org.example.propertyview.entity.Hotel;
import org.example.propertyview.exception.HotelNotFoundException;
import org.example.propertyview.mapper.HotelMapper;
import org.example.propertyview.repository.HotelRepository;
import org.example.propertyview.repository.HotelSpecifications;
import org.example.propertyview.repository.NameCount;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository repository;
    private final HotelMapper mapper;

    public HotelService(HotelRepository repository, HotelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<HotelSummaryDto> findAll() {
        return repository.findAll().stream().map(mapper::toSummary).toList();
    }

    public HotelDetailsDto findById(Long id) {
        return mapper.toDetails(getOrThrow(id));
    }

    public List<HotelSummaryDto> search(String name, String brand, String city, String country, List<String> amenities) {
        Specification<Hotel> spec = HotelSpecifications.nameContains(name)
                .and(HotelSpecifications.equalsIgnoreCase("brand", brand))
                .and(HotelSpecifications.equalsIgnoreCase("city", city))
                .and(HotelSpecifications.equalsIgnoreCase("country", country))
                .and(HotelSpecifications.hasAllAmenities(amenities));
        return repository.findAll(spec).stream().map(mapper::toSummary).toList();
    }

    @Transactional
    public HotelSummaryDto create(HotelCreateRequest request) {
        return mapper.toSummary(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public HotelDetailsDto addAmenities(Long id, List<String> amenities) {
        Hotel hotel = getOrThrow(id);
        if (amenities != null) {
            amenities.stream()
                    .filter(a -> a != null && !a.isBlank())
                    .map(String::trim)
                    .forEach(hotel.getAmenities()::add);
        }
        return mapper.toDetails(hotel);
    }

    public Map<String, Long> histogram(String param) {
        List<NameCount> rows = switch (HistogramParam.from(param)) {
            case BRAND -> repository.countByBrand();
            case CITY -> repository.countByCity();
            case COUNTRY -> repository.countByCountry();
            case AMENITIES -> repository.countByAmenity();
        };
        Map<String, Long> result = new LinkedHashMap<>();
        rows.forEach(r -> result.put(r.name(), r.count()));
        return result;
    }

    private Hotel getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new HotelNotFoundException(id));
    }
}