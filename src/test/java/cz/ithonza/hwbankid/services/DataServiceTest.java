package cz.ithonza.hwbankid.services;

import cz.ithonza.hwbankid.model.DifferenceRate;
import cz.ithonza.hwbankid.model.RateRow;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DataServiceTest {

    @Test
    void getDifferenceRateToTwoCurrencies() {

        DataService dataService = new DataService();
        ReflectionTestUtils.setField(dataService, "roundingScale", 6);
        String currency1 = "USD";
        String currency2 = "EUR";

        RateRow rateRowEUR = new RateRow();
        rateRowEUR.setCurrency("EUR");
        rateRowEUR.setCnbRateForCZK(new BigDecimal("25.0"));
        rateRowEUR.setCode("EMU");
        rateRowEUR.setCountry("EU");
        rateRowEUR.setRateFromOtherSource(new BigDecimal("24.0"));
        dataService.writeToRow("EUR", rateRowEUR);

        RateRow rateRowUSD = new RateRow();
        rateRowUSD.setCurrency("USD");
        rateRowUSD.setCnbRateForCZK(new BigDecimal("20.0"));
        rateRowUSD.setCode("USA");
        rateRowUSD.setCountry("US");
        rateRowUSD.setRateFromOtherSource(new BigDecimal("19.0"));
        dataService.writeToRow("USD", rateRowUSD);


         DifferenceRate differenceRate = dataService.getDifferenceRateToTwoCurrencies(currency1, currency2);

        // Assert
        assertNotNull(differenceRate);
        assertTrue(differenceRate.difference().doubleValue() >= 0, "The difference rate should be non-negative");
    }
}
