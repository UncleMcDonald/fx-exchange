package com.fx.api.feed;

import com.fx.api.repo.RateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FeedService {

    private static final Logger log = LoggerFactory.getLogger(FeedService.class);

    private static final String ACCEPTED = "ACCEPTED";
    private static final String DECLINED = "DECLINED";

    private final RateRepository rates;
    private final AcceptingState accepting;
    private final OrchestratorClient orchestrator;

    public FeedService(RateRepository rates, AcceptingState accepting, OrchestratorClient orchestrator) {
        this.rates = rates;
        this.accepting = accepting;
        this.orchestrator = orchestrator;
    }

    public void handle(IncomingBatch batch) {
        if (!accepting.isAccepting()) {
            log.info("Declined feed batch {} with {} rates", batch.batchId(), batch.rates().size());
            orchestrator.ack(batch.batchId(), DECLINED);
            return;
        }

        int stored = 0;
        for (IncomingRate rate : batch.rates()) {
            stored += rates.insert(rate.base().toUpperCase(), rate.quote().toUpperCase(), rate.rate());
        }

        log.info("Accepted feed batch {} and stored {} rates", batch.batchId(), stored);
        orchestrator.ack(batch.batchId(), ACCEPTED);
    }
}
