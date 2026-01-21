package com.taizhou.kailv.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminEncodingFixer {

    @Autowired
    private JdbcTemplate jdbc;

    @PostMapping("/fix-encoding")
    public String fixEncoding() {
        int totalUpdated = 0;
        // products: id,name,description
        List<Map<String, Object>> products = jdbc.queryForList("SELECT id,name,description FROM products");
        for (Map<String, Object> r : products) {
            Long id = ((Number) r.get("id")).longValue();
            String name = (String) r.get("name");
            String desc = (String) r.get("description");
            String newName = tryFix(name);
            String newDesc = tryFix(desc);
            if (!equalsNullable(name, newName) || !equalsNullable(desc, newDesc)) {
                jdbc.update("UPDATE products SET name=?, description=? WHERE id=?", newName, newDesc, id);
                totalUpdated++;
            }
        }

        // orders: id,status
        List<Map<String, Object>> orders = jdbc.queryForList("SELECT id,status FROM orders");
        for (Map<String, Object> r : orders) {
            Long id = ((Number) r.get("id")).longValue();
            String status = (String) r.get("status");
            String newStatus = tryFix(status);
            if (!equalsNullable(status, newStatus)) {
                jdbc.update("UPDATE orders SET status=? WHERE id=?", newStatus, id);
                totalUpdated++;
            }
        }

        // inventory_records: id,type,note
        List<Map<String, Object>> inv = jdbc.queryForList("SELECT id,type,note FROM inventory_records");
        for (Map<String, Object> r : inv) {
            Long id = ((Number) r.get("id")).longValue();
            String type = (String) r.get("type");
            String note = (String) r.get("note");
            String nType = tryFix(type);
            String nNote = tryFix(note);
            if (!equalsNullable(type, nType) || !equalsNullable(note, nNote)) {
                jdbc.update("UPDATE inventory_records SET type=?, note=? WHERE id=?", nType, nNote, id);
                totalUpdated++;
            }
        }

        // ai_chats: id,reply
        List<Map<String, Object>> chats = jdbc.queryForList("SELECT id,reply FROM ai_chats");
        for (Map<String, Object> r : chats) {
            Long id = ((Number) r.get("id")).longValue();
            String reply = (String) r.get("reply");
            String nReply = tryFix(reply);
            if (!equalsNullable(reply, nReply)) {
                jdbc.update("UPDATE ai_chats SET reply=? WHERE id=?", nReply, id);
                totalUpdated++;
            }
        }

        return "Encoding fix completed. Updated entries: " + totalUpdated;
    }

    @org.springframework.web.bind.annotation.GetMapping("/dump-text")
    public java.util.Map<String, Object> dumpText() {
        java.util.Map<String, Object> out = new java.util.HashMap<>();
        out.put("products", jdbc.queryForList("SELECT id,name,description FROM products"));
        out.put("orders", jdbc.queryForList("SELECT id,status FROM orders"));
        out.put("inventory_records", jdbc.queryForList("SELECT id,product_id,qty,type,note FROM inventory_records"));
        out.put("ai_chats", jdbc.queryForList("SELECT id,message,reply FROM ai_chats"));
        return out;
    }

    private boolean equalsNullable(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private String tryFix(String s) {
        if (s == null) return null;
        // quick heuristic: if contains high-bit-latin sequences like 'å' and no CJK, try re-decode
        boolean hasHigh = false;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) > 127) { hasHigh = true; break; }
        }
        if (!hasHigh) return s;
        try {
            byte[] latin = s.getBytes(StandardCharsets.ISO_8859_1);
            String utf8 = new String(latin, StandardCharsets.UTF_8);
            // if result contains CJK, accept it
            for (int i = 0; i < utf8.length(); i++) {
                char c = utf8.charAt(i);
                if (c >= 0x4e00 && c <= 0x9fff) return utf8;
            }
        } catch (Exception ignored) {}
        return s;
    }
}
