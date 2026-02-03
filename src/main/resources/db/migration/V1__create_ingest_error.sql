create table if not exists ingest_error (
    id uuid primary key,
    created_at timestamptz not null default now(),
    source varchar(50) not null default 'http',
    endpoint varchar(200) not null,
    correlation_id varchar(100),

    error_type varchar(100) not null,
    error_message text not null,

    payload text,             -- raw request body if available
    status varchar(20) not null default 'NEW'  -- NEW, FIXED, REPLAYED, IGNORED
    );

create index if not exists idx_ingest_error_status on ingest_error(status);
create index if not exists idx_ingest_error_created_at on ingest_error(created_at);