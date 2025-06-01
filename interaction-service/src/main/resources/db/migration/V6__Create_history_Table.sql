CREATE TABLE listening_history (
    history_id UUID DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    track_id UUID NOT NULL,
    listened_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (history_id)
);