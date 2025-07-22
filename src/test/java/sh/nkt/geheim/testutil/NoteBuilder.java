package sh.nkt.geheim.testutil;

import sh.nkt.geheim.domain.Note;

import java.time.Instant;
import java.util.UUID;

/**
 * Builder for creating {@link Note} instances with default values.
 */
public class NoteBuilder {
    private UUID id;
    private byte[] ciphertext = "ciphertext".getBytes();
    private byte[] iv = "12-byte---iv".getBytes();
    private byte[] salt = "16-byte-len-salt".getBytes();
    private Instant expiresAt = Instant.now().plusSeconds(3_600);
    private int remainingReads = 1;
    private Instant createdAt = Instant.now();

    public NoteBuilder id(UUID id) {
        this.id = id; return this;
    }

    public NoteBuilder ciphertext(byte[] c) {
        this.ciphertext = c; return this;
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
        this.expiresAt = Instant.now().minusSeconds(30); return this;
    }

    public Note build() {
        return new Note(
            id,
            ciphertext,
            iv,
            salt,
            expiresAt,
            remainingReads,
            createdAt
        );
    }
}
