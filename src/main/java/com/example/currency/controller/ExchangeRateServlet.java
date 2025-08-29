package com.example.currency.controller;

import com.example.currency.dto.ExchangeRateDTO;
import com.example.currency.model.ExchangeRate;
import com.example.currency.service.ExchangeService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ExchangeRateServlet", urlPatterns = {"/exchangeRates/*"})
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeService exchangeService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        exchangeService = new ExchangeService();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // только список всех курсов
            List<ExchangeRate> rates = exchangeService.getAllExchangeRates();
            String json = gson.toJson(rates);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(json);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Ошибка при получении курсов\"}");
        }
    }
}
