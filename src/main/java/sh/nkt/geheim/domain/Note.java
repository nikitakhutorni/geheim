package sh.nkt.geheim.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents an encrypted note in the system.
 * Notes are generated and encrypted by the client using AES-GCM, and stored in the database.
 */
//see this article on AES-GCM:
// https://www.sharesecure.link/articles/the-ultimate-developers-guide-to-aes-gcm-encryption-with-web-cryptography-api
@Table("notes")
public record Note (

    /* unique UID for the note */
    @Id UUID id,

    /* the encrypted note content */
    byte[] ciphertext,

    /* initialization vector, ensures that the same plaintext
     * produces different ciphertexts each time */
    byte[] iv,

    /* used for key derivation, ensures that the same password produces different keys */
    byte[] salt,

    /* time at which note expires and will be deleted */
    Instant expiresAt,

    /* number of reads remaining before the note is deleted */
    int remainingReads,

    /* time at which the note was created */
    Instant createdAt
) {}
