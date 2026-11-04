package org.example.propertyview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.propertyview.dto.HotelCreateRequest;
import org.example.propertyview.dto.HotelDetailsDto;
import org.example.propertyview.dto.HotelSummaryDto;
import org.example.propertyview.service.HotelService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Hotels")
public class HotelController {

    private final HotelService service;

    public HotelController(HotelService service) {
        this.service = service;
    }

    @Operation(summary = "Список всех отелей (краткая информация)")
    @GetMapping("/hotels")
    public List<HotelSummaryDto> getAll() {
        return service.findAll();
    }

    @Operation(summary = "Расширенная информация об отеле")
    @GetMapping("/hotels/{id}")
    public HotelDetailsDto getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Поиск отелей по name, brand, city, country, amenities")
    @GetMapping("/search")
    public List<HotelSummaryDto> search(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) String brand,
                                        @RequestParam(required = false) String city,
                                        @RequestParam(required = false) String country,
                                        @RequestParam(required = false) List<String> amenities) {
        return service.search(name, brand, city, country, amenities);
    }

    @Operation(summary = "Создание отеля")
    @PostMapping("/hotels")
    @ResponseStatus(HttpStatus.CREATED)
    public HotelSummaryDto create(@Valid @RequestBody HotelCreateRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Добавление списка amenities к отелю")
    @PostMapping("/hotels/{id}/amenities")
    public HotelDetailsDto addAmenities(@PathVariable Long id, @RequestBody List<String> amenities) {
        return service.addAmenities(id, amenities);
    }

    @Operation(summary = "Гистограмма: brand, city, country, amenities")
    @GetMapping("/histogram/{param}")
    public Map<String, Long> histogram(@PathVariable String param) {
        return service.histogram(param);
    }
}