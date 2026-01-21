package com.taizhou.kailv.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taizhou.kailv.api.model.Customer;
import org.junit.jupiter.api.Disabled;
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
@Disabled("Customer module replaced by supplier/sales unit")
public class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void list_returnsOk() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk());
    }

    @Test
    void create_and_get_customer() throws Exception {
        Customer c = new Customer();
        c.setName("测试客户");
        String json = objectMapper.writeValueAsString(c);
        String resp = mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Map map = objectMapper.readValue(resp, Map.class);
        Long id = Long.valueOf(map.get("id").toString());
        mockMvc.perform(get("/api/customers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("测试客户"));
    }
}
