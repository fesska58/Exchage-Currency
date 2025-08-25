package com.example.currency.dao;

import com.example.currency.model.Currency;
import com.example.currency.model.ExchangeRate;
import com.example.currency.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDAO {
    private static final ExchangeRateDAO INSTANCE = new ExchangeRateDAO();
    CurrencyDAO currencyDAO = CurrencyDAO.getINSTANCE();
    private ExchangeRateDAO() {}

    public static ExchangeRateDAO getInstance() {
        return INSTANCE;
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public List<ExchangeRate> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        String sql = "SELECT * FROM exchange_rates";

        try(Connection conn = Database.get();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Currency base = currencyDAO.getById(rs.getInt("base_currency_id"));
                Currency target = currencyDAO.getById(rs.getInt("target_currency_id"));
                exchangeRates.add(new ExchangeRate(
                        rs.getInt("id"),
                        base,
                        target,
                        rs.getBigDecimal("rate")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return exchangeRates;
    }

    // Найти курс по парам валют
    public ExchangeRate getRate(String baseCode, String targetCode) throws SQLException {
        String sql = """
            SELECT id, base_currency_id, target_currency_id, rate
            FROM exchange_rates
            WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?)
              AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)
        """;

        try (Connection conn = Database.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, baseCode);
            stmt.setString(2, targetCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Currency base = currencyDAO.getById(rs.getInt("base_currency_id"));
                    Currency target = currencyDAO.getById(rs.getInt("target_currency_id"));

                    return new ExchangeRate(
                            rs.getInt("id"),
                            base,
                            target,
                            rs.getBigDecimal("rate")
                    );
                }
            }
        }
        return null;
    }

    // Добавить новый курс
    public ExchangeRate create(ExchangeRate rate) throws SQLException {
        String sql = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)" +
                " VALUES (?, ?, ?) RETURNING id";

        try (Connection conn = Database.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rate.getBaseCurrency().getId());
            stmt.setInt(2, rate.getTargetCurrency().getId());
            stmt.setBigDecimal(3, rate.getRate());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rate.setId(rs.getInt("id"));
            }
        }
        return rate;
    }
}
