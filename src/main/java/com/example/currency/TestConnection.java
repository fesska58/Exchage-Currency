package com.example.currency;

import com.example.currency.dao.CurrencyDAO;
import com.example.currency.model.Currency;
import com.example.currency.utils.Database;

import java.sql.Driver;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        Class<Driver> driverClass = Driver.class;
        try (var conn = Database.get()) {
            System.out.println("✅ Успешное подключение к PostgreSQL!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.printf("Close CP");
            Database.closePool();
        }

//        CurrencyDAO dao = new CurrencyDAO();
//        try {
//            dao.getAllCurrencies().forEach(c ->
//                    System.out.println(c.getCode() + " - " + c.getFullName())
//            );
//
//            Currency usd = dao.getCurrencyByCode("USD");
//            System.out.println("Found: " + usd.getFullName());
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }finally {
//            Database.closePool();
//        }
//    }
    }
}
