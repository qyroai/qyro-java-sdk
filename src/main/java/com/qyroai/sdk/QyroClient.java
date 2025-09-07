package com.qyroai.sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qyroai.sdk.exceptions.ConfigurationException;
import com.qyroai.sdk.exceptions.HttpException;
import com.qyroai.sdk.models.Message;
import com.qyroai.sdk.models.Session;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QyroClient {
    private final String baseUrl;
    private final String token;
    private final HttpClient http;
    private final ObjectMapper mapper = new ObjectMapper();

    public QyroClient(String baseUrl, String token) {
        this(baseUrl, token, 30.0);
    }

    public QyroClient(String baseUrl, String token, double timeoutSeconds) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new ConfigurationException("base_url is required");
        }
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.token = token;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis((long) (timeoutSeconds * 1000)))
                .build();
    }

    private String url(String path) {
        return baseUrl + path;
    }

    private void raiseIfError(HttpResponse<String> resp) {
        int code = resp.statusCode();
        if (code >= 200 && code < 300)
            return;
        String message;
        try {
            JsonNode node = mapper.readTree(resp.body());
            JsonNode msgNode = node.get("message");
            message = msgNode != null ? msgNode.asText() : node.toString();
        } catch (Exception e) {
            message = resp.body();
        }
        throw new HttpException(code, message, resp);
    }

    private HttpRequest.Builder headers(URI uri) {
        return HttpRequest.newBuilder(uri)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");
    }

    public Session createSession(String assistantId, Map<String, Object> context) {
        String path = "/client/api/v1/assistants/" + assistantId + "/sessions";
        try {
            String payload = mapper.writeValueAsString(Map.of("context", context));
            HttpRequest req = headers(URI.create(url(path)))
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            raiseIfError(resp);
            JsonNode node = mapper.readTree(resp.body());
            return new Session(node.get("id").asText());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Message> fetchSessionMessages(String assistantId, String sessionId) {
        String path = "/client/api/v1/assistants/" + assistantId + "/sessions/" + sessionId + "/messages";
        try {
            HttpRequest req = headers(URI.create(url(path)))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            raiseIfError(resp);
            List<Map<String, Object>> list = mapper.readValue(resp.body(), new TypeReference<>() {
            });
            List<Message> out = new ArrayList<>();
            for (Map<String, Object> m : list) {
                out.add(new Message((String) m.get("id"), (String) m.get("role"), (String) m.get("content")));
            }
            return out;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Message> chat(String assistantId, String sessionId, String message) {
        String path = "/client/api/v1/assistants/" + assistantId + "/sessions/" + sessionId + "/chat";
        try {
            String payload = mapper.writeValueAsString(Map.of("message", message));
            HttpRequest req = headers(URI.create(url(path)))
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            raiseIfError(resp);
            List<Map<String, Object>> list = mapper.readValue(resp.body(), new TypeReference<>() {
            });
            List<Message> out = new ArrayList<>();
            for (Map<String, Object> m : list) {
                out.add(new Message((String) m.get("id"), (String) m.get("role"), (String) m.get("content")));
            }
            return out;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}