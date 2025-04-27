package cz.ithonza.hwbankid.jobs;

import cz.ithonza.hwbankid.services.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class SyncJob {

    private final SyncService syncService;

    @Autowired
    public SyncJob(SyncService syncService) {
        this.syncService = syncService;
    }


    @Scheduled(initialDelay = 2000, fixedRate = 300000)
    public void runJob() {
        syncService.syncJob();
    }
}
