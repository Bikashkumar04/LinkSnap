CREATE TABLE IF NOT EXISTS short_urls (
    id BIGSERIAL PRIMARY KEY,
    original_url TEXT NOT NULL,
    short_code VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    expiry_at TIMESTAMP,
    click_count INTEGER,
    user_id BIGINT,
    is_active BOOLEAN
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(120) NOT NULL,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    last_login_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(512) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

CREATE TABLE IF NOT EXISTS workspaces (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(120) NOT NULL UNIQUE,
    owner_user_id BIGINT NOT NULL,
    plan_tier VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS workspace_members (
    workspace_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(30) NOT NULL,
    invited_at TIMESTAMP NOT NULL,
    joined_at TIMESTAMP,
    PRIMARY KEY (workspace_id, user_id)
);

CREATE TABLE IF NOT EXISTS domains (
    id BIGSERIAL PRIMARY KEY,
    workspace_id BIGINT NOT NULL,
    host VARCHAR(255) NOT NULL UNIQUE,
    is_primary BOOLEAN NOT NULL,
    verification_status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_domains_workspace_id ON domains(workspace_id);

CREATE TABLE IF NOT EXISTS links (
    id BIGSERIAL PRIMARY KEY,
    workspace_id BIGINT NOT NULL,
    domain_id BIGINT NOT NULL,
    created_by_user_id BIGINT NOT NULL,
    short_code VARCHAR(32) NOT NULL,
    original_url TEXT NOT NULL,
    title VARCHAR(255),
    is_active BOOLEAN NOT NULL,
    expires_at TIMESTAMP,
    max_clicks INTEGER,
    password_hash VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_links_domain_short_code ON links(domain_id, short_code);
CREATE INDEX IF NOT EXISTS idx_links_workspace_id ON links(workspace_id);
CREATE INDEX IF NOT EXISTS idx_links_expires_at ON links(expires_at);
CREATE INDEX IF NOT EXISTS idx_links_created_at ON links(created_at);

CREATE TABLE IF NOT EXISTS api_keys (
    id BIGSERIAL PRIMARY KEY,
    workspace_id BIGINT NOT NULL,
    created_by_user_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    key_prefix VARCHAR(32) NOT NULL,
    key_hash VARCHAR(255) NOT NULL,
    scopes TEXT NOT NULL,
    last_used_at TIMESTAMP,
    expires_at TIMESTAMP,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_api_keys_workspace_id ON api_keys(workspace_id);
CREATE INDEX IF NOT EXISTS idx_api_keys_key_prefix ON api_keys(key_prefix);

CREATE TABLE IF NOT EXISTS link_click_events (
    id BIGSERIAL PRIMARY KEY,
    link_id BIGINT NOT NULL,
    clicked_at TIMESTAMP NOT NULL,
    ip_hash VARCHAR(128),
    country_code VARCHAR(10),
    region VARCHAR(120),
    city VARCHAR(120),
    referer VARCHAR(1000),
    user_agent VARCHAR(1024),
    device_type VARCHAR(40),
    browser VARCHAR(80),
    os VARCHAR(80),
    utm_source VARCHAR(120),
    utm_medium VARCHAR(120),
    utm_campaign VARCHAR(120),
    is_bot BOOLEAN NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_link_click_events_link_id ON link_click_events(link_id);
CREATE INDEX IF NOT EXISTS idx_link_click_events_clicked_at ON link_click_events(clicked_at);
CREATE INDEX IF NOT EXISTS idx_link_click_events_link_clicked_at ON link_click_events(link_id, clicked_at);

CREATE TABLE IF NOT EXISTS link_daily_stats (
    link_id BIGINT NOT NULL,
    stat_date DATE NOT NULL,
    total_clicks BIGINT NOT NULL,
    unique_clicks BIGINT NOT NULL,
    bot_clicks BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (link_id, stat_date)
);

CREATE TABLE IF NOT EXISTS link_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    link_id BIGINT NOT NULL,
    actor_user_id BIGINT,
    action VARCHAR(80) NOT NULL,
    before_json TEXT,
    after_json TEXT,
    created_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_link_audit_logs_link_id ON link_audit_logs(link_id);
CREATE INDEX IF NOT EXISTS idx_link_audit_logs_created_at ON link_audit_logs(created_at);
