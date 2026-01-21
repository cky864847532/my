package com.taizhou.kailv.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taizhou.kailv.api.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void list_returnsOk() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    void create_and_get_product() throws Exception {
        Product p = new Product();
        p.setName("测试商品");
        p.setSpec("500g/瓶");
        String json = objectMapper.writeValueAsString(p);
        String resp = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Map map = objectMapper.readValue(resp, Map.class);
        Long id = Long.valueOf(map.get("id").toString());
        mockMvc.perform(get("/api/products/" + id))
                .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("测试商品"))
            .andExpect(jsonPath("$.spec").value("500g/瓶"));
    }
}
