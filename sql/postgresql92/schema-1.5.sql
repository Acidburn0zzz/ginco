-- ALTER TABLE thesaurus_term DROP COLUMN hidden;
ALTER TABLE thesaurus_term ADD COLUMN hidden boolean NOT NULL DEFAULT false;

--ALTER TABLE thesaurus_term_aud DROP COLUMN hidden;
ALTER TABLE thesaurus_term_aud ADD COLUMN hidden boolean DEFAULT false;

--ALTER TABLE thesaurus_term
--  ADD CONSTRAINT chk_hidden_values CHECK (NOT (prefered = true AND hidden = true));