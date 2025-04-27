package cz.ithonza.hwbankid.controler;

/**
 * Controller pro dva endpointy. Vlastne jsou 3. Bohuzel v zadani neni jak ma vypadat vysledek jednou to vracim jako bigdecimal a jednou jako objekt s detaily.
 * Vetsinou ty jednoduché endpointy se pak předělají na ty objektové.
  * Nemám to pod swaggerem. Přidal bych jednu dependenci a vyhodil to ze security
 */

import cz.ithonza.hwbankid.model.DifferenceRate;

import cz.ithonza.hwbankid.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;



@RestController
public class RateController {

    private final DataService dataService;

    @Autowired
    public RateController(DataService dataService) {
        this.dataService = dataService;
    }

    // API: List of supported currency pairs, return simple list
    @GetMapping("/api/currency-pairs")
    public Flux<String> getSupportedCurrencyPairs() {
        return Flux.fromIterable(dataService.getRateRows().keySet());
    }

    // API: Count difference between two currency pairs I return simple difference
    @GetMapping("/api/currency-difference/{currency}/{otherCurrency}")
    public Mono<BigDecimal> getCurrencyDifference(@PathVariable String currency, @PathVariable String otherCurrency) {
        return Mono.just(dataService.getDifferenceRateToTwoCurrencies(currency, otherCurrency).difference());
    }

    //more detailed result.
    @GetMapping("/api/currency-difference/{currency}/{otherCurrency}/detail")
    public Mono<DifferenceRate> getCurrencyDifferenceDetail(@PathVariable String currency, @PathVariable String otherCurrency) {
        return Mono.just(dataService.getDifferenceRateToTwoCurrencies(currency, otherCurrency));
    }
}
