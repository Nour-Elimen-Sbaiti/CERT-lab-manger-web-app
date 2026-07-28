CREATE TABLE interventions (
                               id                          BIGSERIAL PRIMARY KEY,
                               equipement_id                BIGINT NOT NULL REFERENCES equipements(id),
                               type                        VARCHAR(30) NOT NULL
                                   CHECK (type IN ('ETALONNAGE','MAINTENANCE_PREVENTIVE','MAINTENANCE_CORRECTIVE',
                                                   'PANNE','VERIFICATION')),
                               date_intervention             DATE NOT NULL,
                               nature_intervention            TEXT NOT NULL,
                               cout                         NUMERIC(12,3),
                               numero_bon_intervention        VARCHAR(50),
                               intervenant_nom                VARCHAR(150),
                               intervenant_societe             VARCHAR(150),
                               statut                       VARCHAR(20) NOT NULL DEFAULT 'OUVERT'
                                   CHECK (statut IN ('OUVERT','EN_COURS','RESOLU')),
                               created_at                     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_interventions_equipement ON interventions(equipement_id);