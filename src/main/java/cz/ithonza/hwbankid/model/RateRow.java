package cz.ithonza.hwbankid.model;

import java.math.BigDecimal;
import java.util.Date;

public class RateRow {

    private String currency;
    private String country;
    private String code;

    private BigDecimal cnbRateForCZK;
    private Date dateFromCNB;

    private BigDecimal rateFromOtherSource;
    private Date dateFromOtherSource;

    private BigDecimal baseRateForCZK;

    public String getCurrency() {
        return currency;
    }

    public RateRow setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public RateRow setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCode() {
        return code;
    }

    public RateRow setCode(String code) {
        this.code = code;
        return this;
    }

    public BigDecimal getCnbRateForCZK() {
        return cnbRateForCZK;
    }

    public RateRow setCnbRateForCZK(BigDecimal cnbRateForCZK) {
        this.cnbRateForCZK = cnbRateForCZK;
        return this;
    }

    public Date getDateFromCNB() {
        return dateFromCNB;
    }

    public RateRow setDateFromCNB(Date dateFromCNB) {
        this.dateFromCNB = dateFromCNB;
        return this;
    }

    public BigDecimal getRateFromOtherSource() {
        return rateFromOtherSource;
    }

    public RateRow setRateFromOtherSource(BigDecimal rateFromOtherSource) {
        this.rateFromOtherSource = rateFromOtherSource;
        return this;
    }

    public Date getDateFromOtherSource() {
        return dateFromOtherSource;
    }

    public RateRow setDateFromOtherSource(Date dateFromOtherSource) {
        this.dateFromOtherSource = dateFromOtherSource;
        return this;
    }


    public BigDecimal getBaseRateForCZK() {
        return baseRateForCZK;
    }

    public RateRow setBaseRateForCZK(BigDecimal baseRateForCZK) {
        this.baseRateForCZK = baseRateForCZK;
        return this;
    }
}
