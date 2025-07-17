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
        // TODO: this test is erroring
        Note initial = new NoteBuilder().build();
        initial = repo.save(initial);
        UUID id = initial.id();

        // consume first time should work
        var consumedOnce = repo.consume(id);
        assertThat(consumedOnce).isPresent();
        assertThat(consumedOnce.get().remainingReads()).isEqualTo(1);

        // consume second time should NOT work
        var consumedTwice = repo.consume(id);
        assertThat(consumedTwice).isEmpty();
    }
}
