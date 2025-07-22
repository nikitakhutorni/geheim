package sh.nkt.geheim.dto;

import java.util.UUID;

/**
 * DTO for returning a Note ID after creation.
 */
public record NoteCreateResponseDto(
        UUID id
) {}
