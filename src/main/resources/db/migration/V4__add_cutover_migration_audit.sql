CREATE TABLE IF NOT EXISTS cutover_migration_audit (
    id BIGSERIAL PRIMARY KEY,
    migration_name VARCHAR(120) NOT NULL,
    status VARCHAR(20) NOT NULL,
    migrated_count INTEGER NOT NULL,
    message TEXT,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_cutover_migration_name ON cutover_migration_audit(migration_name);
