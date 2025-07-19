package sh.nkt.geheim.testutil;

import sh.nkt.geheim.domain.Note;

import java.time.Instant;
import java.util.UUID;

/**
 * Builder for creating {@link Note} instances with default values.
 */
public class NoteBuilder {
    private UUID id;
    private byte[] cipherText = "dummy".getBytes();
    private byte[] iv = new byte[12];
    private byte[] salt = new byte[16];
    private Instant expiresAt = Instant.now().plusSeconds(3_600);
    private int remainingReads = 1;
    private Instant createdAt = Instant.now();

    public NoteBuilder cipherText(byte[] c) {
        this.cipherText = c; return this;
    }

    public NoteBuilder iv(byte[] iv) {
        this.iv = iv; return this;
    }

    public NoteBuilder salt(byte[] salt) {
        this.salt = salt; return this;
    }

    public NoteBuilder expiresAt(Instant when) {
        this.expiresAt = when; return this;
    }

    public NoteBuilder remainingReads(int n) {
        this.remainingReads = n; return this;
    }

    public NoteBuilder createdAt(Instant when) {
        this.createdAt = when; return this;
    }

    public NoteBuilder expired() {
        this.expiresAt = Instant.now().minusSeconds(1); return this;
    }

    public Note build() {
        return new Note(
            id,
            cipherText,
            iv,
            salt,
            expiresAt,
            remainingReads,
            createdAt
        );
    }
}
