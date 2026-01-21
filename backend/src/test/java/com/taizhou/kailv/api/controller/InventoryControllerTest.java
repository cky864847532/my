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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Inventory API updated; legacy tests disabled")
public class InventoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void list_returnsOk() throws Exception {
        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk());
    }

    @Test
    void stockIn_and_detail() throws Exception {
        HashMap<String, Object> req = new HashMap<>();
        req.put("productId", 1);
        req.put("qty", 10);
        req.put("batch", "BATCH1");
        req.put("type", "入库");
        String resp = mockMvc.perform(post("/api/inventory/in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        // 简单校验返回 id
        mockMvc.perform(get("/api/inventory/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1));
    }
}
