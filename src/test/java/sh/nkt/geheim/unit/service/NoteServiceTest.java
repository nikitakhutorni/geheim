package sh.nkt.geheim.unit.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import sh.nkt.geheim.domain.Note;
import sh.nkt.geheim.dto.NoteCreateRequestDto;
import sh.nkt.geheim.dto.NoteCreateResponseDto;
import sh.nkt.geheim.dto.NoteReadResponseDto;
import sh.nkt.geheim.repository.NoteRepository;
import sh.nkt.geheim.service.NoteMapper;
import sh.nkt.geheim.service.NoteService;
import sh.nkt.geheim.testutil.NoteBuilder;

import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    // creates full mock of the NoteService class
    @Mock
    private NoteRepository repo;

    // creates a real object, while allowing to track every call to its methods
    @Spy
    // TODO: Probably not needed anymore
    private NoteMapper mapper = new NoteMapper();

    // the class under test into which the mocks and spies are injected
    @InjectMocks
    private NoteService svc;

    private static String b64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("saves note and returns its ID")
        void savesAndReturnsId() {
            Instant now = Instant.now();
            UUID expectedId = UUID.randomUUID();

            // note we expect to be saved
            Note saved = new NoteBuilder()
                    .id(expectedId)
                    .remainingReads(1)
                    .expiresAt(now.plusSeconds(10 * 60L))
                    .build();

            // request DTO to create a note
            NoteCreateRequestDto requestDto = new NoteCreateRequestDto(
                    b64(saved.ciphertext()),
                    b64(saved.iv()),
                    b64(saved.salt()),
                    10,
                    1
            );

            // mock the repository to return the saved note when save is called
            when(repo.save(any(Note.class))).thenReturn(saved);

            // act
            NoteCreateResponseDto responseDto = svc.create(requestDto);

            // assert
            assertThat(responseDto.id()).isEqualTo(expectedId);
            verify(repo).save(any(Note.class));
        }
    }

    @Nested
    @DisplayName("readOnce()")
    class ReadOnce {

        @Test
        @DisplayName("1+ reads left after read returns noteDto without deleting note")
        void moreThanOneReadsLeft() {
            UUID id = UUID.randomUUID();

            // we have to set this to 1 because we are mocking the repo
            // and the consume method will return exactly this note
            Note note = new NoteBuilder().id(id).remainingReads(1).build();
            when(repo.consume(id)).thenReturn(Optional.of(note));

            NoteReadResponseDto dto = svc.readOnce(id);

            assertThat(dto.remainingReads()).isEqualTo(1);
            // delete was never called
            verify(repo, never()).delete(any());
        }

        @Test
        @DisplayName("missing note throws 410 Gone")
        void notFound() {
            UUID id = UUID.randomUUID();
            when(repo.consume(id)).thenReturn(Optional.empty());

            ResponseStatusException ex = assertThrows(
                    ResponseStatusException.class,
                    () -> svc.readOnce(id)
            );
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.GONE);
            assertThat(ex.getReason()).isEqualTo("Note is not available");
            // also should not be called
            verify(repo, never()).delete(any());
        }

        @Test
        @DisplayName("expired note is deleted then 410 Gone is thrown")
        void expired() {
            UUID id = UUID.randomUUID();
            Note expired = new NoteBuilder().id(id).expired().build();
            when(repo.consume(id)).thenReturn(Optional.of(expired));

            ResponseStatusException ex = assertThrows(
                    ResponseStatusException.class,
                    () -> svc.readOnce(id)
            );
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.GONE);
            assertThat(ex.getReason()).isEqualTo("Note is expired");
            verify(repo).delete(expired);
        }

        @Test
        @DisplayName("zero reads left will delete note but still return noteDto")
        void zeroReadsRemaining() {
            UUID id = UUID.randomUUID();
            // here too we must set to expected reads value since repo is mocked
            Note last = new NoteBuilder().id(id).remainingReads(0).build();
            when(repo.consume(id)).thenReturn(Optional.of(last));

            NoteReadResponseDto dto = svc.readOnce(id);

            assertThat(dto.remainingReads()).isZero();
            verify(repo).delete(last);
        }
    }
}
