package sh.nkt.geheim.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sh.nkt.geheim.repository.NoteRepository;

import java.time.Clock;
import java.time.Instant;

/**
 * Job for deleting all expired notes.
 */
@Component
public class PurgeJob {

    private static final Logger log = LoggerFactory.getLogger(PurgeJob.class);

    private final NoteRepository noteRepository;
    private final Clock clock;

    /**
     * Constructs a new PurgeJob.
     *
     * @param noteRepository the repository to access notes
     * @param clock          a Clock bean (injected from the configuration)
     */
    public PurgeJob(NoteRepository noteRepository, Clock clock) {
        this.noteRepository = noteRepository;
        this.clock = clock;
    }

    /**
     * Scheduled method that runs every 5 minutes to purge expired notes.
     * It deletes all notes whose TTL has passed.
     */
    @Scheduled(cron = "0 */5 * * * *", zone = "UTC")
    public void purge() {
        Instant now = clock.instant();
        int deletedCount = noteRepository.deleteByExpiresAtBefore(now);
        // added logging to see when purge job runs
        log.info("Purge of {} expired notes completed at {}", deletedCount, now);
    }
}
