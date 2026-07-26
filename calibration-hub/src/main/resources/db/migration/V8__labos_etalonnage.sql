CREATE TABLE labos_etalonnage (
                                  id                  BIGSERIAL PRIMARY KEY,
                                  nom                 VARCHAR(100) NOT NULL UNIQUE,
                                  contact_nom         VARCHAR(100),
                                  contact_prenom      VARCHAR(100),
                                  contact_email       VARCHAR(150),
                                  contact_telephone   VARCHAR(30)
);