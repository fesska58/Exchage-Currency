package com.example.currency.dao;

import com.example.currency.model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO {
    private static final String URL = "jdbc:postgresql://localhost:5432/currency_exchange";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<Currency> getAllCurrencies() throws SQLException {
        List<Currency> currencies = new ArrayList<Currency>();
        String sql = "SELECT * FROM currencies ORDER BY id";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                currencies.add(new Currency(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("fullname"),
                        rs.getString("sign")
                ));
            }
        }

        return currencies;
    }

    public Currency getCurrencyByCode(String code) throws SQLException {
        String sql = "SELECT * FROM currencies WHERE code = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Currency(rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("fullname"),
                            rs.getString("sign")
                    );
                }else {
                    return null;
                }
            }
        }
    }

    public Currency createCurrency(Currency currency) throws SQLException {
        String sql = "INSERT INTO currencies (code, fullname, sign) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currency.getCode());
            stmt.setString(2, currency.getFullName());
            stmt.setString(3, currency.getSign());

            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                currency.setId(rs.getInt(1));
            }
        }
        return currency;
    }
}
