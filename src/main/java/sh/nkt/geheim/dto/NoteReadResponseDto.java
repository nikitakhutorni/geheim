package sh.nkt.geheim.dto;

import java.time.Instant;

/**
 * DTO for returning a Note on a read.
 */
public record NoteReadResponseDto(
        String ciphertext,
        String iv,
        String salt,
        Instant expiresAt,
        int remainingReads
) {}
