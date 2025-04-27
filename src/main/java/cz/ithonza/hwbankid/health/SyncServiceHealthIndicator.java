package cz.ithonza.hwbankid.health;

import cz.ithonza.hwbankid.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class SyncServiceHealthIndicator  implements HealthIndicator {

    private final DataService dataService;

    @Autowired
    public SyncServiceHealthIndicator(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public Health health() {
        final AtomicBoolean isHealthy = new AtomicBoolean(!dataService.getRateRows().isEmpty());
        //check dates for sync if is older then one-hour mark as unhealthy
        if (isHealthy.get()) {
                dataService.getRateRows().values().forEach(rateRow -> {
                    if (rateRow.getDateFromCNB() == null
                            || rateRow.getDateFromCNB().getTime() < System.currentTimeMillis() - 3600000
                            || (
                            rateRow.getDateFromOtherSource() == null
                                    && (rateRow.getDateFromOtherSource().getTime() < System.currentTimeMillis() - 3600000)
                            )
                    ) {
                        isHealthy.set(false);
                    }
                });
        }


        if (isHealthy.get()) {
            return Health.up().withDetail("SyncService", "UP").build();
        } else {
            return Health.down().withDetail("SyncService", "DOWN").build();
        }
    }
}
