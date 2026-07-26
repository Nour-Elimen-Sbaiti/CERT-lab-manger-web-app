CREATE TABLE demandes_prestation (
                                     id                      BIGSERIAL PRIMARY KEY,
                                     etalonnage_id           BIGINT NOT NULL UNIQUE REFERENCES etalonnages(id),
                                     labo_etalonnage_id      BIGINT REFERENCES labos_etalonnage(id),
                                     date_demande            DATE NOT NULL DEFAULT CURRENT_DATE,
                                     etat_a_la_livraison     TEXT,
                                     lieu                    VARCHAR(20) CHECK (lieu IN ('LABORATOIRE', 'SITE')),
                                     comparaison_inter_labo  BOOLEAN NOT NULL DEFAULT FALSE,
                                     declaration_conformite  BOOLEAN NOT NULL DEFAULT FALSE,
                                     regle_decision          VARCHAR(40) CHECK (regle_decision IN
                                                                                ('BINAIRE_SIMPLE', 'BINAIRE_BANDE_GARDE', 'NON_BINAIRE_BANDE_GARDE')),
                                     points_a_etalonner      TEXT,
                                     niveau_incertitude      VARCHAR(15) CHECK (niveau_incertitude IN ('SUPERIEUR_1E_04', 'INFERIEUR_1E_04')),
                                     created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);