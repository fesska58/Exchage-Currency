package com.example.currency.service;

import com.example.currency.dao.CurrencyDAO;
import com.example.currency.exceptions.BadRequestException;
import com.example.currency.exceptions.ConflictException;
import com.example.currency.exceptions.NotFoundException;
import com.example.currency.model.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class CurrencyService {
    private CurrencyDAO currencyDAO;

    public CurrencyService() {
        this.currencyDAO = CurrencyDAO.getINSTANCE();
    }

    public List<Currency> getAllCurrencies() throws SQLException {
        return currencyDAO.getAllCurrencies();
    }

    public Currency getCurrencyByCode(String code) throws SQLException {
        String rawCode = normalizeCode(code);
        Currency currency = currencyDAO.getCurrencyByCode(rawCode);
        if(currency == null) {
            throw new NotFoundException("Валюта не найдена");
        }
        return currency;
    }

    public Currency addCurrency(String name, String code, String sign) throws SQLException {
        if (isBlank(name) || isBlank(code) || isBlank(sign))
            throw new BadRequestException("Отсутствует нужное поле формы");

        String rawCode = normalizeCode(code);

        if (currencyDAO.getCurrencyByCode(rawCode) != null) {
            throw new ConflictException("Валюта с таким кодом уже существует");
        }

        Currency currency = new Currency();
        currency.setCode(code);
        currency.setFullName(name);
        currency.setSign(sign);

        return currencyDAO.createCurrency(currency);
    }

    public Currency addCurrency(Currency currency) throws SQLException {

        return currencyDAO.createCurrency(currency);
    }

    public Currency updateCurrency(int id, String code, String fullName, String sign) throws SQLException {
        if(id <= 0){
            throw new IllegalArgumentException("Некорректный ID");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Код валюты обязателен");
        }

        Currency currency = currencyDAO.updateCurrency(id, code, fullName, sign);
        if(currency == null){
            throw new IllegalArgumentException("Валюта с id=" + id + " не найдена");
        }

        return currency;
    }

    private String normalizeCode(String code) {
        if (isBlank(code)) throw new BadRequestException("Код валюты отсутствует");
        String c = code.trim().toUpperCase(Locale.ROOT);
        if (c.length() != 3 || !c.chars().allMatch(Character::isLetter))
            throw new BadRequestException("Код валюты должен состоять из 3 букв");
        return c;
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
