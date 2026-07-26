CREATE TABLE personnel (
                           id      BIGSERIAL PRIMARY KEY,
                           nom     VARCHAR(100) NOT NULL,
                           prenom  VARCHAR(100),
                           email   VARCHAR(150),
                           telephone VARCHAR(30)
);

CREATE TABLE accessoires (
                             id              BIGSERIAL PRIMARY KEY,
                             equipement_id   BIGINT NOT NULL REFERENCES equipements(id),
                             designation     VARCHAR(200) NOT NULL,
                             marque          VARCHAR(100),
                             numero_serie    VARCHAR(100)
);

ALTER TABLE equipements
    ADD COLUMN responsable_id BIGINT REFERENCES personnel(id),
    ADD COLUMN suppleant_id BIGINT REFERENCES personnel(id),
    ADD COLUMN contact_technique_id BIGINT REFERENCES personnel(id);