package com.fixme.common;

import java.util.HashMap;
import java.util.Map;
import java.net.Socket;

public class FixMessage {
    private static final char SOH = '\u0001';
    private String raw;
    private final Map<Integer, String> fields = new HashMap<>();
    private Socket destination;
    // tag dictionary for readability:
    public static final Map<String, Integer> TAGS = Map.ofEntries(
        Map.entry("BeginString", 8),
        Map.entry("BodyLength", 9),
        Map.entry("MsgType", 35),
        Map.entry("SenderCompID", 49),
        Map.entry("TargetCompID", 56),
        Map.entry("Symbol", 55),
        Map.entry("Side", 54),
        Map.entry("OrderQty", 38),
        Map.entry("Price", 44),
        Map.entry("CheckSum", 10)
    );

    public FixMessage() {}

    // Build from a raw string like: "ID=100001; Target=200001; Type=BUY; Instrument=IBM; Qty=10; Price=135.50"
    public FixMessage(String raw) {
        this.raw = raw;

        String[] parts = raw.split(String.valueOf(SOH));
        for (String part : parts) {
            if (part.trim().isEmpty()) continue;
            String[] pair = part.trim().split("=", 2);
            if (pair.length == 2) {
                fields.put(Integer.parseInt(pair[0].trim()), pair[1].trim());
            }
        }
    }

    public FixMessage set(int tag, String value) {
        fields.put(tag, value);
        return this;
    }

    public FixMessage set(String key, String value) {
        Integer tag = TAGS.get(key);
        if (tag == null) {
            throw new IllegalArgumentException("Unknown FIX tag key: " + key);
        }
        return set(tag, value);
    }

    public FixMessage set(String key, int value) {
        return set(key, String.valueOf(value));
    }

    public FixMessage set(String key, double value) {
        return set(key, String.valueOf(value));
    }

    public void setDestination(Socket destination) {
        this.destination = destination;
    }

    public String getRaw() {
        return raw;
    }

    public String get(int tag) {
        return fields.get(tag);
    }

    public String get(String key) {
        return fields.get(TAGS.get(key));
    }

    public int getInt(String key) {
        return Integer.parseInt(fields.get(TAGS.get(key)));
    }

    public double getDouble(String key) {
        return Double.parseDouble(fields.get(TAGS.get(key)));
    }

    public Socket getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        int bodyLength = calculateBodyLength();
        fields.putIfAbsent(TAGS.get("BeginString"), "FIX.4.4");
        fields.put(TAGS.get("BodyLength"), String.valueOf(bodyLength));
        
        StringBuilder full = new StringBuilder();
        for (Map.Entry<Integer, String> entry : fields.entrySet()) {
            if (entry.getKey() == TAGS.get("CheckSum")) {
                continue; // Skip CheckSum for now
            }
            full.append(entry.getKey()).append("=").append(entry.getValue()).append(SOH);
        }

        int checkSum = calculateCheckSum(full.toString());
        String checkSumStr = String.format("%03d", checkSum);
        fields.put(TAGS.get("CheckSum"), checkSumStr);
        full.append(TAGS.get("CheckSum")).append("=").append(checkSumStr).append(SOH);

        return full.toString();
    }

    public int calculateBodyLength() {
        StringBuilder body = new StringBuilder();
        for (Map.Entry<Integer, String> entry : fields.entrySet()) {
            if (entry.getKey() == TAGS.get("BeginString") || entry.getKey() == TAGS.get("BodyLength") || entry.getKey() == TAGS.get("CheckSum")) {
                continue;
            }
            body.append(entry.getKey()).append("=").append(entry.getValue()).append(SOH);
        }
        return body.length();
    }

    private int calculateCheckSum(String msg) {
        int checkSum = 0;
        for (char c : msg.toCharArray()) {
            checkSum += c;
        }

        return checkSum % 256;
    }

    public String buildWithoutCheckSum() {
        StringBuilder sb = new StringBuilder();
        for (var entry : fields.entrySet()) {
            if (entry.getKey() == TAGS.get("CheckSum")) continue;
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(SOH);
        }
        return sb.toString();
    }
}
