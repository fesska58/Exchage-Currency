package com.example.currency.dao;

import com.example.currency.model.Currency;
import com.example.currency.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO {
    private static final CurrencyDAO INSTANCE = new CurrencyDAO();

    private CurrencyDAO() {
    }

    public static CurrencyDAO getINSTANCE() {
        return INSTANCE;
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Currency getById(int baseCurrencyId) throws SQLException{
        String sql = "SELECT id, code, fullname, sign FROM currencies WHERE id = ?";

        try (Connection conn = Database.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, baseCurrencyId);

            try(ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    return new Currency(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("fullname"),
                        rs.getString("sign")
                    );
                }
            }
        }
        return null;
    }

    public List<Currency> getAllCurrencies() throws SQLException {
        List<Currency> currencies = new ArrayList<Currency>();
        String sql = "SELECT * FROM currencies ORDER BY id";

        try (Connection conn = Database.get();
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
        String sql = """
                SELECT * FROM currencies
                WHERE code = ?
                """;

        try (Connection conn = Database.get();
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
        String sql = """
                INSERT INTO currencies (code, fullname, sign)
                VALUES (?, ?, ?) RETURNING id
                """;

        try (Connection conn = Database.get();
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

    public Currency updateCurrency(int id, String code, String fullName, String sign) throws SQLException {
        String sql = """
                UPDATE currencies
                SET code = ?, fullname = ?, sign = ?
                WHERE id = ?
                RETURNING id, code, fullname, sign
                """;

        try (Connection conn = Database.get()){
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, code);
            ps.setString(2, fullName);
            ps.setString(3, sign);
            ps.setInt(4, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Currency currency = new Currency();
                    currency.setId(rs.getInt("id"));
                    currency.setCode(rs.getString("code"));
                    currency.setFullName(rs.getString("fullname"));
                    currency.setSign(rs.getString("sign"));
                    return currency;
                }else {
                    return null;
                }
            }
        }
    }

    public Currency updateCurrency(Currency currency) throws SQLException {
        return updateCurrency(currency.getId(), currency.getCode(), currency.getFullName(), currency.getSign());
    }

    public boolean deleteCurrency(int id) throws SQLException {
        String sql = "DELETE FROM currencies WHERE id = ?";

        try (Connection conn = Database.get();){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }
}
