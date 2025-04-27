package cz.ithonza.hwbankid.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.*;

class SyncServiceTest {


    private DataService dataService= new DataService();

    private String testCNBData = """
25.04.2025 #80
země|měna|množství|kód|kurz
Austrálie|dolar|1|AUD|14,008
Brazílie|real|1|BRL|3,863
Bulharsko|lev|1|BGN|12,746
Čína|žen-min-pi|1|CNY|3,012
Dánsko|koruna|1|DKK|3,339
EMU|euro|1|EUR|24,930
Filipíny|peso|100|PHP|39,004
Hongkong|dolar|1|HKD|2,830
Indie|rupie|100|INR|25,698
Indonesie|rupie|1000|IDR|1,304
Island|koruna|100|ISK|17,205
Izrael|nový šekel|1|ILS|6,068
Japonsko|jen|100|JPY|15,315
Jižní Afrika|rand|1|ZAR|1,167
Kanada|dolar|1|CAD|15,825
Korejská republika|won|100|KRW|1,525
Maďarsko|forint|100|HUF|6,135
Malajsie|ringgit|1|MYR|5,018
Mexiko|peso|1|MXN|1,118
MMF|ZPČ|1|XDR|29,770
Norsko|koruna|1|NOK|2,105
Nový Zéland|dolar|1|NZD|13,081
Polsko|zlotý|1|PLN|5,842
Rumunsko|leu|1|RON|5,008
Singapur|dolar|1|SGD|16,699
Švédsko|koruna|1|SEK|2,267
Švýcarsko|frank|1|CHF|26,467
Thajsko|baht|100|THB|65,407
Turecko|lira|100|TRY|57,137
USA|dolar|1|USD|21,950
Velká Británie|libra|1|GBP|29,233
            """;

    private String testFranfurktData = """
            {"amount":1.0,"base":"CZK","date":"2025-04-25","rates":{"AUD":0.07139,"BGN":0.07845,"BRL":0.259,"CAD":0.0632,"CHF":0.03779,"CNY":0.33203,"DKK":0.29947,"EUR":0.04011,"GBP":0.03422,"HKD":0.35336,"HUF":16.3075,"IDR":767.82,"ILS":0.16496,"INR":3.8919,"ISK":5.8125,"JPY":6.5305,"KRW":65.579,"MXN":0.89402,"MYR":0.19927,"NOK":0.47501,"NZD":0.07647,"PHP":2.564,"PLN":0.17122,"RON":0.19967,"SEK":0.44131,"SGD":0.05989,"THB":1.528,"TRY":1.7501,"USD":0.04556,"ZAR":0.85687}}
            """;

    @Test
    void testCNBSync() {
        WebClient webClient = mock(WebClient.class);
        SyncService syncService = new SyncService(dataService, webClient);
        syncService.handleCNBFile(testCNBData);
        Assertions.assertEquals(0.0713877784, dataService.getRateRows().get("AUD").getCnbRateForCZK().doubleValue());
        Assertions.assertEquals(6.5295461965, dataService.getRateRows().get("JPY").getCnbRateForCZK().doubleValue());
    }


    @Test
    void testFranfurktSync() {
        WebClient webClient = mock(WebClient.class);
        SyncService syncService = new SyncService(dataService, webClient);
        syncService.handleCNBFile(testCNBData);
        syncService.handleSimpleJsonFileFranfurkt(testFranfurktData);
        Assertions.assertEquals(0.07139, dataService.getRateRows().get("AUD").getRateFromOtherSource().doubleValue());
    }





}
