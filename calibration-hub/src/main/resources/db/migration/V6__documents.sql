CREATE TABLE documents (
                           id                        BIGSERIAL PRIMARY KEY,
                           equipement_id               BIGINT NOT NULL REFERENCES equipements(id),
                           type_document               VARCHAR(30) NOT NULL
                               CHECK (type_document IN ('CERTIFICAT_ETALONNAGE','CONTRAT_MAINTENANCE',
                                                        'RAPPORT','MANUEL','GARANTIE','AUTRE')),
                           nom_fichier                 VARCHAR(255) NOT NULL,
                           chemin_stockage             VARCHAR(500) NOT NULL,
                           intervention_id              BIGINT REFERENCES interventions(id),
                           contrat_maintenance_id         BIGINT REFERENCES contrats_maintenance(id),
                           uploaded_at                 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_documents_equipement ON documents(equipement_id);