CREATE TABLE IF NOT EXISTS tags (
    id BIGSERIAL PRIMARY KEY,
    workspace_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(30),
    created_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_tags_workspace_name ON tags(workspace_id, name);
CREATE INDEX IF NOT EXISTS idx_tags_workspace_id ON tags(workspace_id);

CREATE TABLE IF NOT EXISTS link_tag_map (
    link_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (link_id, tag_id)
);
