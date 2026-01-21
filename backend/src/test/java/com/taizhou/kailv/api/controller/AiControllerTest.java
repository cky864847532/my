package com.taizhou.kailv.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void chat_returnsMockReply_whenNoApiKey() throws Exception {
        var req = new java.util.HashMap<String, Object>();
        req.put("customerId", 1);
        req.put("message", "测试AI接口");
        mockMvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.blankOrNullString())))
                .andExpect(jsonPath("$.source").value(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is("mock"), org.hamcrest.Matchers.containsString("deepseek"))));
    }
}
