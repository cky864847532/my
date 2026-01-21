package com.taizhou.kailv.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.taizhou.kailv.api.service.AIChatService;
import com.taizhou.kailv.api.model.AIChat;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AIChatService aiChatService;

    @Value("${ai.deepseek.api-key:}")
    private String deepseekApiKey;
    @Value("${ai.deepseek.base-url:https://api.deepseek.com}")
    private String deepseekBaseUrl;
    @Value("${ai.deepseek.model:deepseek-chat}")
    private String deepseekModel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RestTemplate buildRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        return new RestTemplate(factory);
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> body) {
        String userMsg = "";
        if (body != null && body.containsKey("message") && body.get("message") != null) {
            userMsg = String.valueOf(body.get("message"));
        }
        String reply = "（AI未配置API KEY，返回示例回复）";
        double confidence = 0.0;
        String source = "mock";
        if (deepseekApiKey != null && !deepseekApiKey.isBlank()) {
            try {
                RestTemplate restTemplate = buildRestTemplate();
                String url = deepseekBaseUrl + "/chat/completions";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(deepseekApiKey);
                Map<String, Object> req = new HashMap<>();
                req.put("model", deepseekModel);
                req.put("messages", java.util.List.of(
                        Map.of("role", "system", "content", "你是台州凯绿化工的智能助手。"),
                        Map.of("role", "user", "content", userMsg)
                ));
                req.put("stream", false);
                HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(req), headers);
                ResponseEntity<Map> resp = restTemplate.postForEntity(url, entity, Map.class);
                Map<?, ?> bodyMap = resp.getBody();
                Object choices = bodyMap == null ? null : bodyMap.get("choices");
                if (choices instanceof java.util.List && !((java.util.List<?>) choices).isEmpty()) {
                    Object firstChoice = ((java.util.List<?>) choices).get(0);
                    Object msg = firstChoice instanceof Map ? ((Map<?, ?>) firstChoice).get("message") : null;
                    if (msg instanceof Map) {
                        Object contentObj = ((Map<?, ?>) msg).get("content");
                        reply = contentObj == null ? "" : contentObj.toString();
                        confidence = 1.0;
                        source = "deepseek";
                    }
                }
            } catch (Exception e) {
                reply = "（AI接口调用失败：" + e.getMessage() + ")";
                source = "deepseek-error";
            }
        }
        AIChat record = new AIChat();
        Long customerId = null;
        if (body != null && body.containsKey("customerId") && body.get("customerId") != null) {
            try {
                customerId = Long.valueOf(String.valueOf(body.get("customerId")));
            } catch (Exception ignore) {}
        }
        record.setCustomerId(customerId);
        record.setMessage(userMsg);
        record.setReply(reply);
        record.setSource(source);
        record.setConfidence(confidence);
        record.setCreatedAt(LocalDateTime.now());
        aiChatService.save(record);
        return ResponseEntity.ok(Map.of("reply", reply, "source", source, "confidence", confidence));
    }
}
