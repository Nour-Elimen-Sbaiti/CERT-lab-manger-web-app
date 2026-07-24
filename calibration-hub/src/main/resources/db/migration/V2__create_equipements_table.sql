CREATE TABLE equipements (
                             id                          BIGSERIAL PRIMARY KEY,
                             code_identifiant            VARCHAR(50)   NOT NULL UNIQUE,
                             code_emplacement            VARCHAR(20),
                             qr_code                     VARCHAR(255)  NOT NULL UNIQUE,
                             designation                 VARCHAR(150)  NOT NULL,
                             marque                      VARCHAR(100)  NOT NULL,
                             modele                      VARCHAR(100)  NOT NULL,
                             numero_serie                VARCHAR(100)  NOT NULL UNIQUE,
                             fournisseur                 VARCHAR(150),
                             date_acquisition            DATE          NOT NULL,
                             date_mise_en_service        DATE,
                             date_reforme                DATE,
                             valeur_origine               NUMERIC(12,3),
                             periodicite_valeur          INTEGER       NOT NULL,
                             periodicite_unite           VARCHAR(10)   NOT NULL DEFAULT 'ANNEE' CHECK (periodicite_unite IN ('MOIS', 'ANNEE')),
                             garantie_jusqu_au           DATE,
                             etat VARCHAR(30) NOT NULL DEFAULT 'NOUVEAU'
                                 CHECK (etat IN ('NOUVEAU','DISPONIBLE','EN_UTILISATION','EN_ETALONNAGE',
                                                 'EN_MAINTENANCE','EN_PANNE','HORS_SERVICE','REFORME')),
                             observations                TEXT,
                             created_at                  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at                  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE etalonnages (
                             id                  BIGSERIAL PRIMARY KEY,
                             equipement_id       BIGINT NOT NULL REFERENCES equipements(id),
                             laboratoire         VARCHAR(150),
                             date_realisation    DATE,
                             date_validite       DATE,
                             resultat            VARCHAR(20) CHECK (resultat IN ('CONFORME','NON_CONFORME','EN_ATTENTE')),
                             certificat_chemin   VARCHAR(500),
                             created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contrats_maintenance (
                                      id              BIGSERIAL PRIMARY KEY,
                                      equipement_id   BIGINT NOT NULL REFERENCES equipements(id),
                                      societe         VARCHAR(150),
                                      numero_contrat  VARCHAR(50),
                                      periode_debut   DATE,
                                      periode_fin     DATE,
                                      conditions      TEXT,
                                      actif           BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE interventions (
                               id                          BIGSERIAL PRIMARY KEY,
                               equipement_id               BIGINT NOT NULL REFERENCES equipements(id),
                               type                        VARCHAR(30) NOT NULL
                                   CHECK (type IN ('ETALONNAGE','MAINTENANCE_PREVENTIVE','MAINTENANCE_CORRECTIVE','PANNE','VERIFICATION')),
                               date_intervention           DATE NOT NULL,
                               nature_intervention         TEXT NOT NULL,
                               cout                        NUMERIC(12,3),
                               numero_bon_intervention     VARCHAR(50),
                               intervenant_nom             VARCHAR(150),
                               intervenant_societe         VARCHAR(150),
                               created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);