package sh.nkt.geheim.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sh.nkt.geheim.domain.Note;
import sh.nkt.geheim.dto.NoteCreateRequestDto;
import sh.nkt.geheim.dto.NoteCreateResponseDto;
import sh.nkt.geheim.dto.NoteReadResponseDto;
import sh.nkt.geheim.repository.NoteRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class NoteService {

    private final NoteRepository repo;
    private final NoteMapper mapper;

    public NoteService(NoteRepository repo, NoteMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    /**
     * Creates a new note from the provided request DTO and saves it to the repository.
     *
     * @param requestDto the DTO containing the note creation details
     * @return a NoteCreateResponseDto containing the ID of the created note
     */
    @Transactional
    public NoteCreateResponseDto create(NoteCreateRequestDto requestDto) {
        Note entity = mapper.toEntity(requestDto);
        Note saved = repo.save(entity);
        return mapper.toCreateResponse(saved);
    }

    /**
     * Reads an encrypted note payload by ID, consuming one read if available and returns it.
     * If the note is not available, returns 410 Gone.
     * If the note is expired, it will be deleted and returns 410 Gone.
     * It the note has no reads left after consuming, deletes it in the repo and returns note payload.
     *
     * @param id the ID of the note to read
     * @return the NoteReadResponseDto containing the note details
     * @throws ResponseStatusException if the note is not available or expired
     */
    // this marker is required, otherwise the transaction will be rolled back
    // and the deletion will not happen.
    // alternatively, we could use @Transactional(propagation = Propagation.REQUIRES_NEW)
    // might use later for more complex cases
    @Transactional(noRollbackFor = ResponseStatusException.class)
    public NoteReadResponseDto readOnce(UUID id) {
        Instant now = Instant.now();

        Note note = repo.consume(id)
                // if no reads remaining or ID does not exist, return 410 Gone
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.GONE, "Note is not available"
                ));

        // check if expired and delete if so
        if (note.expiresAt().isBefore(now)) {
            repo.delete(note);
            throw new ResponseStatusException(HttpStatus.GONE, "Note is expired");
        }

        // check if remaining reads are zero and delete if so
        if (note.remainingReads() == 0) {
            repo.delete(note);
        }

        // return the note payload with encrypted fields
        return mapper.toReadResponse(note);
    }
}
