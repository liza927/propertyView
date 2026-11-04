package org.example.propertyview.mapper;

import org.example.propertyview.dto.*;
import org.example.propertyview.entity.Hotel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class HotelMapper {

    public Hotel toEntity(HotelCreateRequest r) {
        Hotel h = new Hotel();
        h.setName(r.name().trim());
        h.setDescription(r.description());
        h.setBrand(r.brand().trim());
        h.setHouseNumber(r.address().houseNumber());
        h.setStreet(r.address().street().trim());
        h.setCity(r.address().city().trim());
        h.setCountry(r.address().country().trim());
        h.setPostCode(r.address().postCode().trim());
        h.setPhone(r.contacts().phone().trim());
        h.setEmail(r.contacts().email().trim());
        h.setCheckIn(r.arrivalTime().checkIn());
        h.setCheckOut(r.arrivalTime().checkOut());
        return h;
    }

    public HotelSummaryDto toSummary(Hotel h) {
        String address = "%d %s, %s, %s, %s".formatted(
                h.getHouseNumber(), h.getStreet(), h.getCity(), h.getPostCode(), h.getCountry());
        return new HotelSummaryDto(h.getId(), h.getName(), h.getDescription(), address, h.getPhone());
    }

    public HotelDetailsDto toDetails(Hotel h) {
        return new HotelDetailsDto(
                h.getId(),
                h.getName(),
                h.getDescription(),
                h.getBrand(),
                new AddressDto(h.getHouseNumber(), h.getStreet(), h.getCity(), h.getCountry(), h.getPostCode()),
                new ContactsDto(h.getPhone(), h.getEmail()),
                new ArrivalTimeDto(h.getCheckIn(), h.getCheckOut()),
                new ArrayList<>(h.getAmenities()));
    }
}