ALTER TABLE equipements
    ADD COLUMN labo_etalonnage_id BIGINT REFERENCES labos_etalonnage(id);

ALTER TABLE interventions
    ADD COLUMN labo_etalonnage_id BIGINT REFERENCES labos_etalonnage(id);

ALTER TABLE contrats_maintenance
    ADD COLUMN labo_etalonnage_id BIGINT REFERENCES labos_etalonnage(id);