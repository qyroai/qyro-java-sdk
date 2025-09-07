package com.qyroai.sdk.auth;

public class ApiKeyAuth {
    private final String apiKeyId;
    private final String apiKeySecret;

    public ApiKeyAuth(String apiKeyId, String apiKeySecret) {
        this.apiKeyId = apiKeyId;
        this.apiKeySecret = apiKeySecret;
    }

    public String headerValue() {
        return "ApiKey " + apiKeySecret;
    }

    public String getApiKeyId() {
        return apiKeyId;
    }

    public String getApiKeySecret() {
        return apiKeySecret;
    }
}