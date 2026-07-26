CREATE TABLE etalonnages (
                             id               BIGSERIAL PRIMARY KEY,
                             intervention_id    BIGINT NOT NULL UNIQUE REFERENCES interventions(id),
                             date_validite      DATE,
                             emt                VARCHAR(100),
                             bande_garde         VARCHAR(100),
                             resultat           VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE'
                                 CHECK (resultat IN ('CONFORME','NON_CONFORME','EN_ATTENTE'))
);

CREATE INDEX idx_etalonnages_date_validite ON etalonnages(date_validite);