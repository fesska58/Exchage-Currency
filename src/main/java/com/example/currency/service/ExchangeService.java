package com.example.currency.service;

import com.example.currency.dao.CurrencyDAO;
import com.example.currency.dao.ExchangeRateDAO;
import com.example.currency.exceptions.BadRequestException;
import com.example.currency.model.Currency;
import com.example.currency.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class ExchangeService {
    private final ExchangeRateDAO exchangeRateDAO;
    private final CurrencyDAO currencyDAO;

    public ExchangeService() {
        this.exchangeRateDAO = ExchangeRateDAO.getInstance();
        this.currencyDAO = CurrencyDAO.getINSTANCE();
    }

    // 1. Получить все курсы
    public List<ExchangeRate> getAllExchangeRate() {
        return exchangeRateDAO.getAllExchangeRates();
    }

    // 2. Получить курс по валютной паре
    public ExchangeRate getRateByPair(String baseCode, String targetCode) throws SQLException {
        return exchangeRateDAO.getExchangeRate(baseCode, targetCode);
    }

    // 3. Добавить новый курс
    public ExchangeRate addExchangeRate(String baseCode, String targetCode, BigDecimal rate) throws SQLException {
        Currency base = currencyDAO.getCurrencyByCode(baseCode);
        Currency target = currencyDAO.getCurrencyByCode(targetCode);
        int id = currencyDAO.getAllCurrencies().size() + 1;
        ExchangeRate exchangeRate = new ExchangeRate(id, base, target, rate);
        return exchangeRateDAO.createExchangeRate(exchangeRate);
    }


}
