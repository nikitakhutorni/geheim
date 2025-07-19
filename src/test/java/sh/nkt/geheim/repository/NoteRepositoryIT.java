package sh.nkt.geheim.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.testcontainers.containers.PostgreSQLContainer;
import sh.nkt.geheim.domain.Note;
import sh.nkt.geheim.testutil.NoteBuilder;

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
    void consumeIsAtomic() {
        Note initial = new NoteBuilder().build();
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
}
