create table notes (
    id uuid primary key default gen_random_uuid(),
    ciphertext bytea not null,
    iv bytea not null,
    salt bytea not null,
    expires_at timestamp with time zone not null,
    remaining_reads integer not null,
    created_at timestamp with time zone default now()
);
create index on notes (expires_at);
