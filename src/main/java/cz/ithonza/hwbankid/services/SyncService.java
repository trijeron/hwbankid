package cz.ithonza.hwbankid.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.ithonza.hwbankid.model.RateRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Service for syncing data from CNB and Franfurkt.
 * This works with WebClient and Spring Boot Scheduler
 *
 */
@Service
public class SyncService {


    @Value("${sync.cnb.url}")
    private String cnbUrl;

    @Value("${sync.franfurkt.url}")
    private String otherPartyUrl;

    @Value("${currency.rounding:10}")
    private int roundingScale;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DataService dataService;

    private final WebClient webClient;

    @Autowired
    public SyncService(DataService dataService, WebClient webClient) {
        this.dataService = dataService;
        this.webClient = webClient;
    }

    public void syncJob() {
        logger.info("Syncing data");
        readDataFromCNB();
        readDataFromOtherParty();
    }

    public void readDataFromCNB() {
        logger.info("Syncing data from CNB");
        String response = webClient.get()
                .uri(cnbUrl)
                .retrieve()

                .bodyToMono(String.class)
                .retry(3)
                .block();

        if (response != null) {
            logger.debug("Response from CNB: {}", response);
            handleCNBFile(response);
        } else {
            logger.warn("No response from CNB");
        }

        logger.info("Syncing data from CNB finished");
    }

    /**
     * Handle CNB file
     * with format datum|země|měna|kód|kurz and then rows with rates
     * This save into dataService
     * @param response string
     */
    public void handleCNBFile(String response) {
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (!line.matches("\\d{2}\\.\\d{2}\\.\\d{4}.*")
                        && !line.startsWith("země|měna|množství|kód|kurz")) {


                    String[] parts = line.split("\\|");
                    if (parts.length == 5) {
                        int quantity = Integer.parseInt(parts[2].trim());
                        BigDecimal rate = new BigDecimal(parts[4].trim().replace(",", "."));
                        BigDecimal ratePerUnit = rate.divide(BigDecimal.valueOf(quantity), roundingScale, RoundingMode.HALF_DOWN);
                        BigDecimal invertedValueForBaseOneCZK = BigDecimal.ONE.divide(ratePerUnit, roundingScale, RoundingMode.HALF_DOWN);
                        RateRow rateRow = new RateRow()
                                .setCountry(parts[0].trim())
                                .setCurrency(parts[1].trim())
                                .setCode(parts[3].trim())
                                .setCnbRateForCZK(invertedValueForBaseOneCZK)
                                .setDateFromCNB(new Date());
                        dataService.writeToRow(rateRow.getCode(), rateRow);
                    }
                }
            }

    }

    public void readDataFromOtherParty() {
        logger.info("Syncing data from other party");
        if (dataService.getRateRows().isEmpty()) {
            logger.error("No data to add sync from CNB");
            return;
        }

        String response = webClient.get()
                .uri(otherPartyUrl)
                .retrieve()

                .bodyToMono(String.class)
                .retry(3)
                .block();

        if (response != null) {
            logger.debug("Response from CNB: {}", response);
            handleSimpleJsonFileFranfurkt(response);
        } else {
            logger.warn("No response from CNB");
        }

        logger.info("Syncing data from other party finished");
    }

    /**
     * Handle a simple JSON file
     * with format currency: rate underrates node
     * This adds data to existing dataService rows
     *
     */
    public void handleSimpleJsonFileFranfurkt(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode currencyNode = rootNode.get("rates");
            for (String currency : dataService.getRateRows().keySet()) {
                if (currencyNode.has(currency)) {
                    BigDecimal rate = currencyNode.get(currency).decimalValue();
                    RateRow rateRow = dataService.getRateRows().get(currency);
                    rateRow.setRateFromOtherSource(rate);
                    rateRow.setDateFromOtherSource(new Date());
                    rateRow.setRateFromOtherSource(rate); //I ask directly for rate for CZK
                    dataService.writeToRow(rateRow.getCode(), rateRow);
                }
            }

        } catch (Exception e) {
            logger.error("Error parsing JSON response", e);
        }


    }

}
