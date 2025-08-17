package com.fixme.common;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class FixMessage {
    private String raw;
    private final Map<String, String> fields = new HashMap<>();

    public FixMessage() {}

    // Build from a raw string like: "ID=100001; Target=200001; Type=BUY; Instrument=IBM; Qty=10; Price=135.50"
    public FixMessage(String raw) {
        this.raw = raw;

        String[] parts = raw.split(";");
        for (String part : parts) {
            String[] pair = part.trim().split("=", 2);
            if (pair.length == 2) {
                fields.put(pair[0].trim(), pair[1].trim());
            }
        }
    }

    public FixMessage set(String key, String value) {
        fields.put(key, value);
        return this;
    }

    public FixMessage set(String key, int value) {
        return set(key, String.valueOf(value));
    }

    public FixMessage set(String key, double value) {
        return set(key, String.valueOf(value));
    }

    public String getRaw() {
        return raw;
    }

    public String get(String key) {
        return fields.get(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(fields.get(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(fields.get(key));
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("; ");
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            joiner.add(entry.getKey() + "=" + entry.getValue());
        }

        return joiner.toString();
    }
}
