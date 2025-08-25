package com.example.currency.service;

import com.example.currency.dto.CurrencyDTO;
import com.example.currency.model.Currency;

public class CurrencyMapper {
    public static CurrencyDTO toDTO(Currency currency) {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setCode(currency.getCode());
        dto.setFullName(currency.getFullName());
        dto.setSign(currency.getSign());
        return dto;
    }

    public static Currency fromDTO(CurrencyDTO dto) {
        Currency currency = new Currency();
        currency.setCode(dto.getCode());
        currency.setFullName(dto.getFullName());
        currency.setSign(dto.getSign());
        return currency;
    }
}
