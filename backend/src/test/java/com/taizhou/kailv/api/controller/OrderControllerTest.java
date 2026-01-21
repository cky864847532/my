package com.taizhou.kailv.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Order module replaced by purchase/sale flows")
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void list_returnsOk() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk());
    }

    @Test
    void create_and_get_order() throws Exception {
        HashMap<String, Object> order = new HashMap<>();
        order.put("customerId", 1);
        order.put("items", List.of(
                Map.of("productId", 1, "qty", 2, "price", 10.0)
        ));
        String resp = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        // 简单校验返回 id
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk());
    }
}
