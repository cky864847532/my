package com.taizhou.kailv.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StatsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void sales_returnsOk() throws Exception {
        mockMvc.perform(get("/api/stats/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void topProducts_returnsOk() throws Exception {
        mockMvc.perform(get("/api/stats/products/top?limit=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void topSalesUnits_returnsOk() throws Exception {
        mockMvc.perform(get("/api/stats/sales-units/top?limit=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void topCustomers_alias_returnsOk() throws Exception {
        mockMvc.perform(get("/api/stats/customers/top?limit=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }
}
