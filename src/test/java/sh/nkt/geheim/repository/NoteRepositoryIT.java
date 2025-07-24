package sh.nkt.geheim.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.testcontainers.containers.PostgreSQLContainer;
import sh.nkt.geheim.domain.Note;
import sh.nkt.geheim.testutil.NoteBuilder;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
public class NoteRepositoryIT {

    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    NoteRepository repo;

    @Test
    @DisplayName("consume() decrements remaining reads and returns the note if it has remaining reads")
    void consume_DecrementsReads() {
        Note initial = new NoteBuilder().remainingReads(1).build();
        initial = repo.save(initial);
        UUID id = initial.id();

        // consume first time should work
        var consumed = repo.consume(id);
        assertThat(consumed).isPresent();
        assertThat(consumed.get().remainingReads()).isEqualTo(0);

        // consume second time should NOT work
        var consumedAgain = repo.consume(id);
        assertThat(consumedAgain).isEmpty();
    }

    @Test
    @DisplayName("deleteByExpiresAtBefore() removes all expired notes")
    void deleteByExpiresAtBefore_removesOnlyExpiredNotes() {
        Instant now = Instant.now();
        Note expired = new NoteBuilder().expired().build();
        Note future = new NoteBuilder().expiresAt(now.plusSeconds(600)).build();

        UUID expiredId = repo.save(expired).id();
        UUID futureId = repo.save(future).id();

        int deletedCount = repo.deleteByExpiresAtBefore(now);

        assertThat(deletedCount).isEqualTo(1);
        assertThat(repo.findById(expiredId)).isEmpty();
        assertThat(repo.findById(futureId)).isPresent();
    }
}
