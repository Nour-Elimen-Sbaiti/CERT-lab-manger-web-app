CREATE TABLE equipements (
                             id                       BIGSERIAL PRIMARY KEY,
                             code_identifiant         VARCHAR(50)  NOT NULL UNIQUE,
                             code_emplacement         VARCHAR(20),
                             qr_code                  VARCHAR(255) NOT NULL UNIQUE,
                             designation               VARCHAR(150) NOT NULL,
                             marque                    VARCHAR(100) NOT NULL,
                             modele                    VARCHAR(100) NOT NULL,
                             numero_serie              VARCHAR(100) NOT NULL UNIQUE,
                             fournisseur                VARCHAR(150),
                             date_acquisition           DATE NOT NULL,
                             date_mise_en_service       DATE,
                             date_reforme                DATE,
                             valeur_origine              NUMERIC(12,3),
                             periodicite_valeur          INTEGER NOT NULL,
                             periodicite_unite           VARCHAR(10) NOT NULL DEFAULT 'ANNEE'
                                 CHECK (periodicite_unite IN ('MOIS', 'ANNEE')),
                             garantie_jusqu_au            DATE,
                             etat                        VARCHAR(30) NOT NULL DEFAULT 'NOUVEAU'
                                 CHECK (etat IN ('NOUVEAU','DISPONIBLE','EN_UTILISATION','EN_ETALONNAGE',
                                                 'EN_MAINTENANCE','EN_PANNE','HORS_SERVICE','REFORME')),
                             observations                 TEXT,
                             created_at                    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at                    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);