package cz.ithonza.hwbankid.model;

import java.math.BigDecimal;

public record DifferenceRate (String currency,
                              String currency2,
                              BigDecimal rateCNB,
                              BigDecimal rateOtherSource,
                              BigDecimal difference) {

    @Override
    public String toString() {
        return "DifferenceRate{" +
                "currency='" + currency + '\'' +
                ", currency2='" + currency2 + '\'' +
                ", rateCNB='" + rateCNB + '\'' +
                ", rateOtherSource='" + rateOtherSource + '\'' +
                ", difference=" + difference +
                '}';
    }
}
