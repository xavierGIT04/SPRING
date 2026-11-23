-- Étape 9c-coût : apprentissage du coût de construction par standing.
-- Un drapeau d'intégration distinct de celui du coefficient de zone : une même vente peut nourrir les deux
-- apprentissages, mais à des moments différents (le coût n'apprend qu'une fois le quartier assez calibré).

ALTER TABLE public.transaction_immobiliere
    ADD COLUMN integre_cout boolean NOT NULL DEFAULT false,
    ADD COLUMN date_integration_cout timestamp(6) with time zone;

ALTER TABLE public.standing
    ADD COLUMN nombre_transactions_calibrage integer NOT NULL DEFAULT 0;