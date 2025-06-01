CREATE TABLE tracks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    s3key VARCHAR(255) NOT NULL UNIQUE
);