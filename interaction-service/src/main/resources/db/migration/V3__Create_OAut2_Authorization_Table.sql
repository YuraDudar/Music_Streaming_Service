CREATE TABLE oauth2_authorization (
    id varchar(100) NOT NULL,
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorization_grant_type varchar(100) NOT NULL,
    authorized_scopes varchar(1000) DEFAULT NULL,
    attributes TEXT DEFAULT NULL,
    state varchar(500) DEFAULT NULL,
    authorization_request TEXT,
    authorization_response TEXT,
    authorization_code_value TEXT DEFAULT NULL,
    authorization_code_issued_at timestamp DEFAULT NULL,
    authorization_code_expires_at timestamp DEFAULT NULL,
    authorization_code_metadata TEXT DEFAULT NULL,
    access_token_value TEXT DEFAULT NULL,
    access_token_issued_at timestamp DEFAULT NULL,
    access_token_expires_at timestamp DEFAULT NULL,
    access_token_metadata TEXT DEFAULT NULL,
    access_token_type varchar(100) DEFAULT NULL,
    access_token_scopes varchar(1000) DEFAULT NULL,
    oidc_id_token_value TEXT DEFAULT NULL,
    oidc_id_token_issued_at timestamp DEFAULT NULL,
    oidc_id_token_expires_at timestamp DEFAULT NULL,
    oidc_id_token_metadata TEXT DEFAULT NULL,
    refresh_token_value TEXT DEFAULT NULL,
    refresh_token_issued_at timestamp DEFAULT NULL,
    refresh_token_expires_at timestamp DEFAULT NULL,
    refresh_token_metadata TEXT DEFAULT NULL,
    user_code_value TEXT DEFAULT NULL,
    user_code_issued_at timestamp DEFAULT NULL,
    user_code_expires_at timestamp DEFAULT NULL,
    user_code_metadata TEXT DEFAULT NULL,
    device_code_value TEXT DEFAULT NULL,
    device_code_issued_at timestamp DEFAULT NULL,
    device_code_expires_at timestamp DEFAULT NULL,
    device_code_metadata TEXT DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_auth_registered_client_id ON oauth2_authorization(registered_client_id);
CREATE INDEX idx_auth_principal_name ON oauth2_authorization(principal_name);
CREATE INDEX idx_auth_grant_type ON oauth2_authorization(authorization_grant_type);
CREATE INDEX idx_auth_state ON oauth2_authorization(state);
CREATE INDEX idx_auth_auth_code ON oauth2_authorization(authorization_code_value); -- Индекс по префиксу для TEXT
CREATE INDEX idx_auth_access_token ON oauth2_authorization(access_token_value); -- Индекс по префиксу для TEXT
CREATE INDEX idx_auth_refresh_token ON oauth2_authorization(refresh_token_value); -- Индекс по префиксу для TEXT