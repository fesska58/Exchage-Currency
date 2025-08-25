package com.example.currency.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    public static void writeJson(HttpServletResponse resp, int status, Object payload) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(gson.toJson(payload));
    }

    public static void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        writeJson(resp, status, new ErrorResponse(message));
    }

    public static class ErrorResponse {
        public final String message;
        public ErrorResponse(String message) { this.message = message; }
    }
}
