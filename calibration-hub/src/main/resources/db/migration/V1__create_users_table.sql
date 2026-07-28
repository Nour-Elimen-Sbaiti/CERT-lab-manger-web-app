CREATE TABLE users (
                       id              BIGSERIAL PRIMARY KEY,
                       username        VARCHAR(50)  NOT NULL UNIQUE,
                       password        VARCHAR(255) NOT NULL,
                       nom             VARCHAR(100) NOT NULL,
                       prenom          VARCHAR(100) NOT NULL,
                       email           VARCHAR(150) NOT NULL UNIQUE,
                       role            VARCHAR(30)  NOT NULL,
                       enabled         BOOLEAN      NOT NULL DEFAULT FALSE,
                       email_verified  BOOLEAN      NOT NULL DEFAULT FALSE,
                       created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE login_history (
                               id              BIGSERIAL PRIMARY KEY,
                               user_id         BIGINT      NOT NULL REFERENCES users(id),
                               login_time      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               ip_address      VARCHAR(45),
                               success         BOOLEAN     NOT NULL
);

CREATE TABLE verification_token (
                                    id              BIGSERIAL PRIMARY KEY,
                                    user_id         BIGINT       NOT NULL REFERENCES users(id),
                                    token           VARCHAR(255) NOT NULL UNIQUE,
                                    expires_at      TIMESTAMP    NOT NULL,
                                    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE password_reset_token (
                                      id              BIGSERIAL PRIMARY KEY,
                                      user_id         BIGINT       NOT NULL REFERENCES users(id),
                                      token           VARCHAR(255) NOT NULL UNIQUE,
                                      expires_at      TIMESTAMP    NOT NULL,
                                      used            BOOLEAN      NOT NULL DEFAULT FALSE,
                                      created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);