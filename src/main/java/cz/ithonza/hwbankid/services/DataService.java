package cz.ithonza.hwbankid.services;

import cz.ithonza.hwbankid.model.DifferenceRate;
import cz.ithonza.hwbankid.model.RateRow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Tato trida je zodpovedna za praci s daty.
 * Obsahuje metody pro ziskani a zapis dat do pameti. Synchronizase je pro to ze v jednu chvili muze probihat synchronizace dat z jobu a cteni pro API.
 * Standardne data byvaji v nejake db, ktera si to resi sama a pak by synchronize odpadl
 * Tato trida by mela byt nahrazena praci s databazi nebo jinym ulozistem.
 */
@Service
public class DataService {

    // Tohle by standardne bylo v redisu
    private static final HashMap<String, RateRow> rateRows = new HashMap<>();
    public static final String CZK = "CZK";

    @Value("${currency.rounding:10}")
    private int roundingScale;

    public Map<String, RateRow> getRateRows() {
        synchronized (rateRows) {
            return new HashMap<>(rateRows);
        }
    }

    public void writeToRow(String currency, RateRow rateRow) {
        synchronized (rateRows) {
            var row  = rateRows.get(currency);
            if (row == null) {
                rateRows.put(currency, rateRow);
            } else {
                row.setCnbRateForCZK(rateRow.getCnbRateForCZK());
                row.setDateFromCNB(rateRow.getDateFromCNB());
                row.setRateFromOtherSource(row.getRateFromOtherSource());
                row.setDateFromOtherSource(rateRow.getDateFromOtherSource());
            }
        }
    }

    /**
     * Vracím jednoduchý rozdíl mezi dvěma měnami.
     * @param currency
     * @param currencyTwo
     * @return
     */
    public DifferenceRate getDifferenceRateToTwoCurrencies(String currency, String currencyTwo) {

        synchronized (rateRows) {
            if (CZK.equals(currency)) {
                return getDifferenceForCZK(currencyTwo, true);
            }
            if (CZK.equals(currencyTwo)) {
                return getDifferenceForCZK(currency, false);
            }

            RateRow rateRow1 = rateRows.get(currency);
            RateRow rateRow2 = rateRows.get(currencyTwo);

            if (rateRow1 == null || rateRow2 == null) {
                throw new IllegalArgumentException("Jedna z měn není podporována.");
            }

            //tady zaleží na pžístupu firmy, jestli má radši ukecanější kód nebo ne. Tohle zvyšuje čitelnost, ale vytváří se zbytečné objekty.
            BigDecimal rate1ToCZKCNB = rateRow1.getCnbRateForCZK();
            BigDecimal rate2ToCZKCNB = rateRow2.getCnbRateForCZK();

            BigDecimal rateToCZKFrankfurt1 = rateRow1.getRateFromOtherSource();
            BigDecimal rateToCZKFrankfurt2 = rateRow2.getRateFromOtherSource();

            if (rateToCZKFrankfurt1 == null || rateToCZKFrankfurt2 == null) {
                throw new IllegalStateException("Chybí data pro výpočet kurzu.");
            }

            if (rate1ToCZKCNB == null || rate2ToCZKCNB == null) {
                throw new IllegalStateException("Chybí data pro výpočet kurzu.");
            }

            BigDecimal rateCNB =  rate1ToCZKCNB.divide(rate2ToCZKCNB,roundingScale,  RoundingMode.HALF_UP);
            BigDecimal rateFrankfurt = rateToCZKFrankfurt1.divide(rateToCZKFrankfurt2,roundingScale,  RoundingMode.HALF_UP);

            return new DifferenceRate(currency, currencyTwo, rateCNB, rateFrankfurt, rateCNB.subtract(rateFrankfurt));
        }
    }


    private static DifferenceRate getDifferenceForCZK(String currency, boolean czkIsFirst) {
        var row =  rateRows.get(currency);
        if (row == null) {
            throw new IllegalArgumentException("Jedna z měn není podporována.");
        }
        var difference = czkIsFirst? row.getCnbRateForCZK().subtract(row.getRateFromOtherSource()) : row.getRateFromOtherSource().subtract(row.getCnbRateForCZK());

        return new DifferenceRate("CZK", currency, row.getCnbRateForCZK(), row.getRateFromOtherSource(), difference);

    }


}
