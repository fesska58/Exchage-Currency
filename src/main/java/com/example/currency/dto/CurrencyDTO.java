package com.example.currency.dto;

public class CurrencyDTO {
    private String code;
    private String fullName;
    private String sign;

    public CurrencyDTO() {}

    public CurrencyDTO(String code, String name, String sign) {
        this.code = code;
        this.fullName = name;
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
