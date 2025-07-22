package sh.nkt.geheim.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.PostgreSQLContainer;
import sh.nkt.geheim.domain.Note;
import sh.nkt.geheim.dto.NoteCreateRequestDto;
import sh.nkt.geheim.repository.NoteRepository;
import sh.nkt.geheim.service.NoteService;
import sh.nkt.geheim.testutil.NoteBuilder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class NoteServiceIT {
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    NoteService svc;

    @Autowired
    NoteRepository repo;

    @Test
    @DisplayName("creating a note with 2 reads and reading it twice deletes note")
    void createThenReadThenGone() {
        NoteCreateRequestDto requestDto = new NoteCreateRequestDto(
                "encodedText",
                "encodedIvValue",
                "encodedSaltTextVal",
                1,
                2
        );
        UUID id = svc.create(requestDto).id();

        assertThat(svc.readOnce(id).remainingReads()).isEqualTo(1);
        assertThat(svc.readOnce(id).remainingReads()).isEqualTo(0);
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> svc.readOnce(id)
        );
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.GONE);
        assertThat(ex.getReason()).isEqualTo("Note is not available");
    }

    @Test
    @DisplayName("expired note is deleted and throws error on read")
    void expiredNoteIsDeleted() {
        Note expired = repo.save(new NoteBuilder().expired().build());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> svc.readOnce(expired.id())
        );
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.GONE);
        assertThat(ex.getReason()).isEqualTo("Note is expired");

        // check that the note is deleted
        assertThat(repo.findById(expired.id())).isEmpty();
    }
}
