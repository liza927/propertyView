package org.example.propertyview;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HotelApiTest {

    @Autowired
    MockMvc mvc;

    private static final String NEW_HOTEL = """
            {
              "name": "Test Hotel",
              "brand": "TestBrand",
              "address": {"houseNumber": 5, "street": "Main St", "city": "Gomel", "country": "Belarus", "postCode": "246000"},
              "contacts": {"phone": "+375 232 00-00-00", "email": "test@hotel.com"},
              "arrivalTime": {"checkIn": "15:00"}
            }
            """;

    @Test
    void seededHotelIsReturned() throws Exception {
        mvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address", is("9 Pobediteley Avenue, Minsk, 220004, Belarus")));
    }

    @Test
    void createSearchAndAmenities() throws Exception {
        mvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content(NEW_HOTEL))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address", is("5 Main St, Gomel, 246000, Belarus")));

        mvc.perform(get("/search").param("city", "gomel").param("brand", "testbrand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Hotel")));

        String id = mvc.perform(get("/search").param("city", "Gomel")).andReturn().getResponse()
                .getContentAsString().replaceAll(".*\"id\":(\\d+).*", "$1");

        mvc.perform(post("/hotels/" + id + "/amenities").contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Free WiFi\", \"Sauna\"]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amenities", containsInAnyOrder("Free WiFi", "Sauna")));

        mvc.perform(get("/search").param("amenities", "free wifi,sauna"))
                .andExpect(jsonPath("$", hasSize(1)));

        mvc.perform(get("/histogram/amenities")).andExpect(jsonPath("$.Sauna", is(1)));
        mvc.perform(get("/histogram/city")).andExpect(jsonPath("$.Minsk", is(1)));
    }

    @Test
    void errors() throws Exception {
        mvc.perform(get("/hotels/99999")).andExpect(status().isNotFound());
        mvc.perform(get("/histogram/unknown")).andExpect(status().isBadRequest());
        mvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}