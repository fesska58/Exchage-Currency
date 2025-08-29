package com.example.currency.service;

import com.example.currency.dto.ExchangeRateDTO;
import com.example.currency.model.Currency;
import com.example.currency.model.ExchangeRate;

public class ExchangeMapper {
    private final CurrencyMapper currencyMapper = new CurrencyMapper();

    public ExchangeRateDTO toDTO(ExchangeRate exchangeRate) {
        if (exchangeRate == null) {
            return null;
        }

        return new ExchangeRateDTO(
                exchangeRate.getId(),
                currencyMapper.toDTO(exchangeRate.getBaseCurrency()),
                currencyMapper.toDTO(exchangeRate.getTargetCurrency()),
                exchangeRate.getRate()
        );
    }

    public ExchangeRate fromDTO(ExchangeRateDTO dto) {
        if (dto == null) {
            return null;
        }

        Currency base = currencyMapper.fromDTO(dto.getBaseCurrency());
        Currency target = currencyMapper.fromDTO(dto.getTargetCurrency());

        return new ExchangeRate(
                dto.getId(),
                base,
                target,
                dto.getRate()
        );
    }
}
