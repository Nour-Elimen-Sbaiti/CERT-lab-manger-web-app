CREATE TABLE alertes_resolues (
    id              BIGSERIAL PRIMARY KEY,
    alerte_key      VARCHAR(160) NOT NULL UNIQUE,
    type            VARCHAR(50)  NOT NULL,
    source_id       BIGINT       NOT NULL,
    equipement_id   BIGINT       REFERENCES equipements(id),
    date_echeance   DATE,
    observation     TEXT,
    resolved_by     VARCHAR(100),
    resolved_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_alertes_resolues_type_source ON alertes_resolues(type, source_id);
CREATE INDEX idx_alertes_resolues_equipement ON alertes_resolues(equipement_id);
