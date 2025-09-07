package com.qyroai.sdk.exceptions;

import java.net.http.HttpResponse;

public class HttpException extends QyroException {
    private final int statusCode;
    private final String responseBody;
    private final HttpResponse<String> response;

    public HttpException(int statusCode, String message, HttpResponse<String> response) {
        super("HTTP " + statusCode + ": " + message);
        this.statusCode = statusCode;
        this.response = response;
        this.responseBody = response != null ? response.body() : null;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public HttpResponse<String> getResponse() {
        return response;
    }
}