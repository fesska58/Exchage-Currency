package com.example.currency.service;

import com.example.currency.dao.CurrencyDAO;
import com.example.currency.dao.ExchangeRateDAO;
import com.example.currency.dto.ExchangeRateDTO;
import com.example.currency.exceptions.BadRequestException;
import com.example.currency.model.Currency;
import com.example.currency.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ExchangeService {
    private final ExchangeRateDAO exchangeRateDAO;
    private final CurrencyDAO currencyDAO;
    private final ExchangeMapper exchangeMapper;

    public ExchangeService() {
        this.exchangeRateDAO = ExchangeRateDAO.getInstance();
        this.currencyDAO = CurrencyDAO.getINSTANCE();
        this.exchangeMapper = new ExchangeMapper();
    }

    public List<ExchangeRate> getAllExchangeRates() throws SQLException {
        return exchangeRateDAO.getAllExchangeRates();
    }

    public ExchangeRate getExchangeRateByCodes(String baseCode, String targetCode) throws SQLException {
        Currency base = currencyDAO.getCurrencyByCode(baseCode);
        Currency target = currencyDAO.getCurrencyByCode(targetCode);

        if (base == null || target == null) return null;

        return exchangeRateDAO.getExchangeRate(base.getCode(), target.getCode());
    }

    public ExchangeRate addExchangeRate(String baseCode, String targetCode, BigDecimal rate) throws SQLException {
        Currency base = currencyDAO.getCurrencyByCode(baseCode);
        Currency target = currencyDAO.getCurrencyByCode(targetCode);

        if (base == null || target == null) {
            throw new SQLException("Одна из валют не найдена");
        }

        ExchangeRate exchangeRate = new ExchangeRate(0, base, target, rate);
        return exchangeRateDAO.createExchangeRate(exchangeRate);
    }

    public void updateExchangeRate(String baseCode, String targetCode, BigDecimal newRate) throws SQLException {
        Currency base = currencyDAO.getCurrencyByCode(baseCode);
        Currency target = currencyDAO.getCurrencyByCode(targetCode);

        if (base == null || target == null)
            throw new SQLException("Одна из валют не найдена");;

        ExchangeRate rate = exchangeRateDAO.getExchangeRate(base.getCode(), target.getCode());

        exchangeRateDAO.updateExchangeRate(rate);
    }

    public BigDecimal exchange(String fromCode, String toCode, BigDecimal amount) throws SQLException {
        if(fromCode.equals(toCode)) {
            return amount;
        }

        // прямой курс
        ExchangeRate direct = exchangeRateDAO.getExchangeRate(fromCode, toCode);
        if(direct != null) {
            return amount.multiply(direct.getRate());
        }

        // обратный курс
        ExchangeRate reverse = exchangeRateDAO.getExchangeRate(fromCode, toCode);
        if(reverse != null) {
            return amount.divide(reverse.getRate(), 4, BigDecimal.ROUND_HALF_UP);
        }

        // через USD
        ExchangeRate fromUSD = getExchangeRateByCodes("USD", fromCode);
        ExchangeRate toUSD = getExchangeRateByCodes("USD", toCode);
        if (fromUSD != null && toUSD != null) {
            BigDecimal usdAmount = amount.divide(fromUSD.getRate(), 4, BigDecimal.ROUND_HALF_UP);
            return usdAmount.multiply(toUSD.getRate());
        }

        throw new SQLException("Курс обмена не найден");
    }
}
