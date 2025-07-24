package sh.nkt.geheim.unit.service.jobs;

import org.junit.jupiter.api.Test;
import sh.nkt.geheim.jobs.PurgeJob;
import sh.nkt.geheim.repository.NoteRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.mockito.Mockito.*;

public class PurgeJobTest {

    @Test
    void purge_delegatesToRepo() {
        Instant fixedNow = Instant.now().minusSeconds(600);
        Clock fixedClock = Clock.fixed(fixedNow, ZoneOffset.UTC);

        // Mocking the repo with static mock()
        NoteRepository repo = mock(NoteRepository.class);
        PurgeJob job = new PurgeJob(repo, fixedClock);

        job.purge();

        // verify that the deleteByExpiresAtBefore method was called with the fixed time
        verify(repo, times(1)).deleteByExpiresAtBefore(fixedNow);
    }
}
