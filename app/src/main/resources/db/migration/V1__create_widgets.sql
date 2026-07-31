CREATE TABLE widgets (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX widgets_created_at_idx ON widgets (created_at DESC);
