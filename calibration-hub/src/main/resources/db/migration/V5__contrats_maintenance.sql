CREATE TABLE contrats_maintenance (
                                      id               BIGSERIAL PRIMARY KEY,
                                      equipement_id     BIGINT NOT NULL REFERENCES equipements(id),
                                      prestataire       VARCHAR(150) NOT NULL,
                                      date_debut         DATE NOT NULL,
                                      date_fin           DATE NOT NULL,
                                      periode_valeur      INTEGER NOT NULL,
                                      periode_unite      VARCHAR(10) NOT NULL
                                          CHECK (periode_unite IN ('MOIS', 'ANNEE')),
                                      created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contrats_equipement ON contrats_maintenance(equipement_id);