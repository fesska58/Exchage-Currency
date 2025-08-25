package com.example.currency;

import com.example.currency.controller.CurrencyServlet;
import com.example.currency.dao.CurrencyDAO;
import com.example.currency.dao.ExchangeRateDAO;
import com.example.currency.exceptions.DaoException;
import com.example.currency.model.Currency;
import com.example.currency.model.ExchangeRate;
import com.example.currency.service.CurrencyService;
import com.example.currency.utils.Database;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import java.math.BigDecimal;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;

public class TestConnection {
    public static void main(String[] args) throws SQLException, ServletException {
//        Class<Driver> driverClass = Driver.class;
//        try (var conn = Database.get()) {
//            System.out.println("✅ Успешное подключение к PostgreSQL!");
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            System.out.printf("Close CP");
//            Database.closePool();
//        }

//        var currencyDao = CurrencyDAO.getINSTANCE();
//        try {
//            currencyDao.getAllCurrencies().forEach(c ->
//                    System.out.println(c.getCode() + " - " + c.getFullName()));
//
//            Currency usd = currencyDao.getCurrencyByCode("USD");
//
//            System.out.println("Found: " + usd.getFullName());
//        } catch (SQLException e) {
//            throw new DaoException(e);
//        }
    }
}
