package sh.nkt.geheim.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;


/**
 * DTO for creating a new note.
 */
public record NoteCreateRequestDto(
        @NotBlank String ciphertext,
        @NotBlank String iv,
        @NotBlank String salt,
        @Positive @Min(1) @Max(60 * 24 * 7) Integer expiresInMinutes,
        @Positive @Min(1) @Max(10) Integer maxReads
) {}
