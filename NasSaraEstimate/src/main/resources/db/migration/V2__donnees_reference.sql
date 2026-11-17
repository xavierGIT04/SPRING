--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

--
-- Data for Name: parametre_global; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (180.000000, '2026-11-16 10:52:59.914474+00', '2026-11-16 10:52:59.914474+00', 1, 0, 'COURANT', 'jours', 'MARCHE_FENETRE_JOURS', 'Âge maximal des annonces prises en compte. Au-delà, une annonce est jugée trop ancienne. Ex. : 180 = six mois.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (2.000000, '2026-11-16 10:53:00.035678+00', '2026-11-16 10:53:00.035678+00', 2, 0, 'COURANT', 'nombre', 'MARCHE_MIN_COMPARABLES', 'Nombre minimal d''annonces comparables pour que la comparaison de marché donne un résultat. En dessous, cette méthode est écartée.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (10.000000, '2026-11-16 10:53:00.050144+00', '2026-11-16 10:53:00.050144+00', 3, 0, 'COURANT', 'nombre', 'MARCHE_MAX_COMPARABLES', 'Nombre maximal d''annonces retenues (les plus proches en surface, puis les plus récentes).') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.300000, '2026-11-16 10:53:00.062096+00', '2026-11-16 10:53:00.062096+00', 4, 0, 'COURANT', 'fraction', 'MARCHE_TOLERANCE_SURFACE', 'Écart de surface accepté entre une annonce et le bien. Ex. : 0,30 = on compare avec des terrains de 30 % plus petits à 30 % plus grands.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (600.000000, '2026-11-16 10:53:00.07216+00', '2026-11-16 10:53:00.07216+00', 5, 0, 'COURANT', 'm²', 'MARCHE_SURFACE_LOT_M2', 'Surface d''un lot, utilisée quand une annonce indique un nombre de lots au lieu d''une surface.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.900000, '2026-11-16 10:53:00.084171+00', '2026-11-16 10:53:00.084171+00', 6, 0, 'COURANT', 'facteur', 'MARCHE_DECOTE_NEGOCIATION_DEFAUT', 'Tant que l''agence n''a pas assez de ventes validées, on suppose qu''un bien se vend à ce niveau du prix affiché. Ex. : 0,90 = 10 % moins cher que l''annonce.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (5.000000, '2026-11-16 10:53:00.094804+00', '2026-11-16 10:53:00.094804+00', 7, 0, 'COURANT', 'nombre', 'MARCHE_MIN_TRANSACTIONS_CORRECTION', 'Nombre de ventes validées dans le quartier à partir duquel l''outil mesure lui-même l''écart entre prix affiché et prix de vente.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.100000, '2026-11-16 10:53:00.104482+00', '2026-11-16 10:53:00.104482+00', 8, 0, 'COURANT', 'fraction', 'HEDONIQUE_MARGE_FOURCHETTE', 'Largeur de la fourchette autour du prix de la méthode 1. Ex. : 0,10 = de 10 % en dessous à 10 % au-dessus.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.080000, '2026-11-16 10:53:00.114966+00', '2026-11-16 10:53:00.114966+00', 9, 0, 'COURANT', 'fraction', 'CAPITALISATION_TAUX_DEFAUT', 'Rendement annuel attendu (loyer annuel ÷ prix) quand le quartier n''a pas le sien. Ex. : 0,08 = 8 %.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.150000, '2026-11-16 10:53:00.126048+00', '2026-11-16 10:53:00.126048+00', 10, 0, 'COURANT', 'fraction', 'CAPITALISATION_TAUX_CHARGES', 'Part du loyer perdue en vacance locative, charges et impôts. Ex. : 0,15 = 15 % du loyer n''est pas encaissé net.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (2.000000, '2026-11-16 10:53:00.135529+00', '2026-11-16 10:53:00.135529+00', 11, 0, 'COURANT', 'nombre', 'CAPITALISATION_MIN_COMPARABLES', 'Nombre minimal de loyers comparables pour s''appuyer sur le marché. En dessous, le loyer actuel du bien est utilisé s''il est renseigné.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (10.000000, '2026-11-16 10:53:00.143056+00', '2026-11-16 10:53:00.143056+00', 12, 0, 'COURANT', 'nuits', 'CAPITALISATION_NUITEES_PAR_MOIS', 'Nuits payées par mois pour convertir un tarif à la nuitée en loyer mensuel. 0 = les annonces à la nuitée sont ignorées.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.750000, '2026-11-16 10:53:00.153303+00', '2026-11-16 10:53:00.153303+00', 13, 0, 'COURANT', 'facteur', 'CAPITALISATION_FACTEUR_MEUBLE', 'Un loyer meublé est ramené à un équivalent non meublé en le multipliant par ce nombre. Ex. : 0,75 = le meublé coûte un tiers de plus.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.400000, '2026-11-16 10:53:00.163303+00', '2026-11-16 10:53:00.163303+00', 14, 0, 'AVANCE', 'facteur', 'HEDONIQUE_CUMUL_COEFFICIENTS_MIN', 'Avancé. Plancher du cumul des coefficients du site (voie, topographie, statut, viabilisation) : évite qu''une accumulation de défauts fasse trop chuter le prix.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (1.800000, '2026-11-16 10:53:00.171351+00', '2026-11-16 10:53:00.171351+00', 15, 0, 'AVANCE', 'facteur', 'HEDONIQUE_CUMUL_COEFFICIENTS_MAX', 'Avancé. Plafond du cumul des coefficients du site : évite qu''une accumulation d''atouts fasse trop monter le prix.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.300000, '2026-11-16 10:53:00.178406+00', '2026-11-16 10:53:00.178406+00', 16, 0, 'AVANCE', 'fraction', 'HEDONIQUE_CONFIANCE_MIN', 'Avancé. Niveau de confiance de la méthode 1 pour un quartier jamais calibré sur des ventes réelles.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.850000, '2026-11-16 10:53:00.185477+00', '2026-11-16 10:53:00.185477+00', 17, 0, 'AVANCE', 'fraction', 'HEDONIQUE_CONFIANCE_MAX', 'Avancé. Niveau de confiance de la méthode 1 pour un quartier bien calibré.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (20.000000, '2026-11-16 10:53:00.192475+00', '2026-11-16 10:53:00.192475+00', 18, 0, 'AVANCE', 'nombre', 'HEDONIQUE_TRANSACTIONS_CALIBRAGE_CIBLE', 'Avancé. Nombre de ventes validées dans un quartier pour que la méthode 1 atteigne son niveau de confiance maximal.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.600000, '2026-11-16 10:53:00.200526+00', '2026-11-16 10:53:00.200526+00', 19, 0, 'AVANCE', 'facteur', 'HEDONIQUE_CONFIANCE_FACTEUR_PROVISOIRE', 'Avancé. Réduction de confiance quand le prix de base du quartier est provisoire (saisi à chaud, non confirmé).') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.700000, '2026-11-16 10:53:00.208577+00', '2026-11-16 10:53:00.208577+00', 20, 0, 'AVANCE', 'facteur', 'MARCHE_CORRECTION_MIN', 'Avancé. Plancher du facteur prix de vente ÷ prix affiché mesuré sur les ventes réelles. Ex. : 0,70 = on n''accepte pas plus de 30 % de baisse.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (1.100000, '2026-11-16 10:53:00.214792+00', '2026-11-16 10:53:00.215579+00', 21, 0, 'AVANCE', 'facteur', 'MARCHE_CORRECTION_MAX', 'Avancé. Plafond du même facteur. Ex. : 1,10 = on n''accepte pas plus de 10 % de hausse par rapport au prix affiché.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.200000, '2026-11-16 10:53:00.223578+00', '2026-11-16 10:53:00.223578+00', 22, 0, 'AVANCE', 'fraction', 'MARCHE_CONFIANCE_MIN', 'Avancé. Confiance minimale de la comparaison de marché (peu d''annonces).') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.800000, '2026-11-16 10:53:00.231498+00', '2026-11-16 10:53:00.231498+00', 23, 0, 'AVANCE', 'fraction', 'MARCHE_CONFIANCE_MAX', 'Avancé. Confiance maximale de la comparaison de marché (beaucoup d''annonces homogènes).') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (1.000000, '2026-11-16 10:53:00.238717+00', '2026-11-16 10:53:00.238717+00', 24, 0, 'AVANCE', 'facteur', 'MARCHE_PENALITE_DISPERSION', 'Avancé. Poids de la dispersion des prix dans la confiance : plus les annonces sont hétérogènes, plus la confiance baisse. 0 = ignorée.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.700000, '2026-11-16 10:53:00.246645+00', '2026-11-16 10:53:00.246645+00', 25, 0, 'AVANCE', 'facteur', 'MARCHE_CONFIANCE_FACTEUR_DECOTE_DEFAUT', 'Avancé. Réduction de confiance quand la décote par défaut est utilisée faute de ventes validées.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.150000, '2026-11-16 10:53:00.254923+00', '2026-11-16 10:53:00.254923+00', 26, 0, 'AVANCE', 'fraction', 'CAPITALISATION_MARGE_FOURCHETTE', 'Avancé. Marge autour du loyer actuel du bien quand il sert de base faute de loyers de marché.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.250000, '2026-11-16 10:53:00.264182+00', '2026-11-16 10:53:00.264182+00', 27, 0, 'AVANCE', 'fraction', 'CAPITALISATION_ECART_ALERTE_LOYER_BIEN', 'Avancé. Écart entre le loyer du bien et le loyer de marché au-delà duquel un avertissement est affiché.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.200000, '2026-11-16 10:53:00.272174+00', '2026-11-16 10:53:00.272174+00', 28, 0, 'AVANCE', 'fraction', 'CAPITALISATION_CONFIANCE_MIN', 'Avancé. Confiance minimale de la capitalisation.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.800000, '2026-11-16 10:53:00.282254+00', '2026-11-16 10:53:00.282254+00', 29, 0, 'AVANCE', 'fraction', 'CAPITALISATION_CONFIANCE_MAX', 'Avancé. Confiance maximale de la capitalisation.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (1.000000, '2026-11-16 10:53:00.291241+00', '2026-11-16 10:53:00.291241+00', 30, 0, 'AVANCE', 'facteur', 'CAPITALISATION_PENALITE_DISPERSION', 'Avancé. Poids de la dispersion des loyers dans la confiance. 0 = ignorée.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.800000, '2026-11-16 10:53:00.300161+00', '2026-11-16 10:53:00.300161+00', 31, 0, 'AVANCE', 'facteur', 'CAPITALISATION_CONFIANCE_FACTEUR_TAUX_DEFAUT', 'Avancé. Réduction de confiance quand le taux de capitalisation est celui par défaut et non celui du quartier.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.600000, '2026-11-16 10:53:00.309231+00', '2026-11-16 10:53:00.309231+00', 32, 0, 'AVANCE', 'facteur', 'CAPITALISATION_CONFIANCE_FACTEUR_LOYER_BIEN', 'Avancé. Réduction de confiance quand la base est le loyer du bien et non le marché.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.300000, '2026-11-16 10:53:00.320302+00', '2026-11-16 10:53:00.320302+00', 33, 0, 'AVANCE', 'fraction', 'FUSION_ECART_ALERTE_METHODES', 'Avancé. Écart entre les prix des méthodes au-delà duquel un avertissement est affiché. Ex. : 0,30 = 30 %.') ON CONFLICT DO NOTHING;
INSERT INTO public.parametre_global (valeur, date_creation, date_modification, id, version, niveau, unite, cle, description) VALUES (0.800000, '2026-11-16 10:53:00.327384+00', '2026-11-16 10:53:00.327384+00', 34, 0, 'AVANCE', 'facteur', 'FUSION_CONFIANCE_FACTEUR_METHODE_UNIQUE', 'Avancé. Réduction de confiance quand une seule méthode a pu conclure (aucun recoupement).') ON CONFLICT DO NOTHING;


--
-- Data for Name: ponderation_methode; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.ponderation_methode (active, poids, date_creation, date_modification, id, version, nature_bien, methode) VALUES (true, 1.0000, '2026-11-17 11:28:18.631053+00', '2026-11-17 11:28:18.631053+00', 7, 0, 'TERRAIN', 'HEDONIQUE') ON CONFLICT DO NOTHING;
INSERT INTO public.ponderation_methode (active, poids, date_creation, date_modification, id, version, nature_bien, methode) VALUES (true, 1.0000, '2026-11-17 11:28:27.116502+00', '2026-11-17 11:28:27.116502+00', 8, 0, 'TERRAIN', 'COMPARAISON_MARCHE') ON CONFLICT DO NOTHING;
INSERT INTO public.ponderation_methode (active, poids, date_creation, date_modification, id, version, nature_bien, methode) VALUES (true, 0.0000, '2026-11-17 11:28:35.811117+00', '2026-11-17 11:28:35.811117+00', 9, 0, 'TERRAIN', 'CAPITALISATION') ON CONFLICT DO NOTHING;
INSERT INTO public.ponderation_methode (active, poids, date_creation, date_modification, id, version, nature_bien, methode) VALUES (true, 1.0000, '2026-11-17 11:28:49.440931+00', '2026-11-17 11:28:49.440931+00', 10, 0, 'BATI', 'HEDONIQUE') ON CONFLICT DO NOTHING;
INSERT INTO public.ponderation_methode (active, poids, date_creation, date_modification, id, version, nature_bien, methode) VALUES (true, 1.0000, '2026-11-17 11:29:01.182088+00', '2026-11-17 11:29:01.182088+00', 11, 0, 'BATI', 'COMPARAISON_MARCHE') ON CONFLICT DO NOTHING;
INSERT INTO public.ponderation_methode (active, poids, date_creation, date_modification, id, version, nature_bien, methode) VALUES (true, 1.0000, '2026-11-17 11:29:08.519599+00', '2026-11-17 11:29:08.519599+00', 12, 0, 'BATI', 'CAPITALISATION') ON CONFLICT DO NOTHING;


--
-- Data for Name: quartier; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 38000.00, 0.0900, '2026-11-17 11:31:26.626534+00', '2026-11-17 11:31:26.626534+00', 4, 0, 'ADETIKOPE', 'Adétikopé', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 83000.00, 0.0800, '2026-11-17 12:21:45.25221+00', '2026-11-17 12:21:45.25221+00', 5, 0, 'ADIDOGOME', 'Adidogomé', 'Wognomé; Avénou') ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 133000.00, 0.0700, '2026-11-17 12:28:26.647749+00', '2026-11-17 12:28:26.647749+00', 6, 0, 'AFLAO GAKLI', 'Aflao Gakli', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 79000.00, 0.0800, '2026-11-17 12:37:22.888804+00', '2026-11-17 12:37:22.888804+00', 8, 0, 'AGOE', 'Agoè', 'Assiyéyé; Cacvéli') ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 133000.00, 0.0700, '2026-11-17 12:36:00.862057+00', '2026-11-17 12:37:36.398981+00', 7, 1, 'AGBALEPEDO', 'Agbalépédo', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 58000.00, 0.0800, '2026-11-17 14:53:22.262684+00', '2026-11-17 14:53:22.262684+00', 9, 0, 'AGOE TELESOU', 'Agoè Télessou', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 58000.00, 0.0800, '2026-11-17 14:54:27.074836+00', '2026-11-17 14:54:27.074836+00', 10, 0, 'AGOE SOGBOSSITO', 'Agoe Sogbossito', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 117000.00, 0.0700, '2026-11-17 14:55:26.088623+00', '2026-11-17 14:55:26.088623+00', 11, 0, 'AVEDJI', 'Avédji', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 75000.00, 0.0800, '2026-11-17 14:57:44.719618+00', '2026-11-17 14:57:44.719618+00', 12, 0, 'AVEPOZO', 'Avépozo', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 100000.00, 0.0750, '2026-11-17 14:58:27.09039+00', '2026-11-17 14:58:27.09039+00', 13, 0, 'BAGUIDA', 'Baguida', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 150000.00, 0.0700, '2026-11-17 15:00:24.842568+00', '2026-11-17 15:00:24.842568+00', 14, 0, 'BE', 'Bè', 'Kpota; Chateau') ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 750000.00, 0.0500, '2026-11-17 15:04:32.156658+00', '2026-11-17 15:04:32.156658+00', 15, 0, 'LOME II', 'Lomé II', 'Cité OUA') ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 21000.00, 0.0950, '2026-11-17 15:05:40.008822+00', '2026-11-17 15:05:40.008822+00', 16, 0, 'DAVIE', 'Davié', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 17000.00, 0.0950, '2026-11-17 15:06:41.664107+00', '2026-11-17 15:06:41.664107+00', 17, 0, 'DJAGBLE', 'Djagblé', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 117000.00, 0.0700, '2026-11-17 15:07:34.387821+00', '2026-11-17 15:07:34.387821+00', 18, 0, 'DJIDJOLE', 'Djidjolé', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 192000.00, 0.0600, '2026-11-17 15:08:52.730412+00', '2026-11-17 15:08:52.730412+00', 19, 0, 'HEDZRANAWOE', 'Hédzranawoé', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 133000.00, 0.0700, '2026-11-17 15:11:09.538674+00', '2026-11-17 15:11:09.538674+00', 20, 0, 'KEGUE', 'Kégué', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 267000.00, 0.0600, '2026-11-17 15:13:03.264329+00', '2026-11-17 15:13:03.264352+00', 21, 0, 'KODJOVIAKOPE', 'Kodjoviakopé', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 50000.00, 0.0900, '2026-11-17 15:15:58.097729+00', '2026-11-17 15:15:58.097729+00', 22, 0, 'KPOGAN', 'Kpogan', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 46000.00, 0.0900, '2026-11-17 15:16:37.970789+00', '2026-11-17 15:16:37.970789+00', 23, 0, 'LEGBASSITO', 'Légbassito', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 375000.00, 0.0600, '2026-11-17 15:18:30.233222+00', '2026-11-17 15:18:30.233222+00', 24, 0, 'CENTRE VILLE', 'Centre Ville', 'Nyékonakpoè') ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 42000.00, 0.0900, '2026-11-17 15:24:21.371588+00', '2026-11-17 15:24:21.371588+00', 25, 0, 'SAGBADO', 'Sagbado', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 33000.00, 0.0900, '2026-11-17 15:25:19.458462+00', '2026-11-17 15:25:19.458462+00', 26, 0, 'SANGUERA', 'Sanguéra', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 28000.00, 0.0900, '2026-11-17 15:26:19.503747+00', '2026-11-17 15:26:19.503747+00', 27, 0, 'SEGBE', 'Segbe', NULL) ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 192000.00, 0.0600, '2026-11-17 15:28:46.510537+00', '2026-11-17 15:28:46.510537+00', 28, 0, 'TOKOIN', 'Tokoin', 'Wuiti; Habitat') ON CONFLICT DO NOTHING;
INSERT INTO public.quartier (actif, coefficient_zone, nombre_transactions_calibrage, prix_base_provisoire, prix_base_terrain_m2, taux_capitalisation, date_creation, date_modification, id, version, code, libelle, localites) VALUES (true, 1.0000, 0, true, 108000.00, 0.0700, '2026-11-17 15:29:42.99112+00', '2026-11-17 15:29:42.99112+00', 29, 0, 'TOTSI', 'Totsi', NULL) ON CONFLICT DO NOTHING;


--
-- Data for Name: standing; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.standing (actif, cout_construction_m2, plafond_vetuste, taux_vetuste_annuel, date_creation, date_modification, id, version, code, libelle) VALUES (true, 250000.00, 0.7000, 0.0500, '2026-11-16 11:36:56.566768+00', '2026-11-16 11:36:56.566768+00', 2, 0, 'MOYEN', 'Moyen') ON CONFLICT DO NOTHING;
INSERT INTO public.standing (actif, cout_construction_m2, plafond_vetuste, taux_vetuste_annuel, date_creation, date_modification, id, version, code, libelle) VALUES (true, 150000.00, 0.7500, 0.0900, '2026-11-16 11:35:50.849109+00', '2026-11-16 11:37:42.678923+00', 1, 2, 'ECONOMIQUE', 'Economique') ON CONFLICT DO NOTHING;
INSERT INTO public.standing (actif, cout_construction_m2, plafond_vetuste, taux_vetuste_annuel, date_creation, date_modification, id, version, code, libelle) VALUES (true, 400000.00, 0.6000, 0.0200, '2026-11-16 11:38:28.876241+00', '2026-11-16 11:38:28.876241+00', 3, 0, 'HAUT-GAME', 'Haut de Game') ON CONFLICT DO NOTHING;


--
-- Data for Name: statut_juridique; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.statut_juridique (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 0.5000, '2026-11-16 11:16:28.290544+00', '2026-11-16 11:16:36.655884+00', 1, 1, 'LITIGE', 'Litige en cours / Reçu simple') ON CONFLICT DO NOTHING;
INSERT INTO public.statut_juridique (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 1.2500, '2026-11-16 11:18:08.706421+00', '2026-11-16 11:18:08.706421+00', 3, 0, 'TITRE-FONCIER', 'Titre Foncier') ON CONFLICT DO NOTHING;
INSERT INTO public.statut_juridique (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 1.0000, '2026-11-16 11:17:24.507857+00', '2026-11-16 14:35:18.993689+00', 2, 1, 'NOTARIE', 'Acte notarié/Plan Visé (PV)/Tampon OTR') ON CONFLICT DO NOTHING;


--
-- Data for Name: topographie; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.topographie (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 0.5000, '2026-11-16 11:13:23.772407+00', '2026-11-16 11:13:23.772407+00', 1, 0, 'BAS-FOND', 'Bas-fond / Marécage / Zone inondable') ON CONFLICT DO NOTHING;
INSERT INTO public.topographie (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 0.8000, '2026-11-16 11:14:02.851632+00', '2026-11-16 11:14:02.851632+00', 2, 0, 'CREUX', 'Terrain en contrebas de la route') ON CONFLICT DO NOTHING;
INSERT INTO public.topographie (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 1.0000, '2026-11-16 11:14:39.469443+00', '2026-11-16 11:14:39.469443+00', 3, 0, 'PLAT', 'Terrain plat / Niveau de la route') ON CONFLICT DO NOTHING;
INSERT INTO public.topographie (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 1.1000, '2026-11-16 11:15:18.70633+00', '2026-11-16 11:15:18.70633+00', 4, 0, 'SURELEVE', 'Terrain légèrement surélevé') ON CONFLICT DO NOTHING;


--
-- Data for Name: type_bien; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.type_bien (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 0.5500, '2026-11-16 11:28:42.158525+00', '2026-11-16 11:28:42.158525+00', 1, 0, 'COUR-COMMUNE', 'Cour Commune') ON CONFLICT DO NOTHING;
INSERT INTO public.type_bien (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 0.9000, '2026-11-16 11:29:20.254361+00', '2026-11-16 11:29:20.254361+00', 2, 0, 'APPARTEMENT', 'Appartement standard') ON CONFLICT DO NOTHING;
INSERT INTO public.type_bien (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 1.0000, '2026-11-16 11:30:19.468068+00', '2026-11-16 11:30:19.468068+00', 3, 0, 'VILLA-CLASSIQUE', 'Villa') ON CONFLICT DO NOTHING;


--
-- Data for Name: type_equipement; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.type_equipement (actif, montant_reference, date_creation, date_modification, id, version, code, libelle) VALUES (true, 2500000.00, '2026-11-16 11:39:05.692625+00', '2026-11-16 11:39:12.782497+00', 1, 1, 'FORAGE', 'Forage') ON CONFLICT DO NOTHING;
INSERT INTO public.type_equipement (actif, montant_reference, date_creation, date_modification, id, version, code, libelle) VALUES (true, 350000.00, '2026-11-16 11:39:56.414084+00', '2026-11-16 11:39:56.414084+00', 2, 0, 'GENERATEUR-ELECTRIQUE', 'Générateur Electrique') ON CONFLICT DO NOTHING;
INSERT INTO public.type_equipement (actif, montant_reference, date_creation, date_modification, id, version, code, libelle) VALUES (true, 800000.00, '2026-11-16 11:41:09.962244+00', '2026-11-16 11:41:09.962244+00', 3, 0, 'GENERATEUR-SOLAIRE', 'Générateur Solaire') ON CONFLICT DO NOTHING;


--
-- Data for Name: type_voie; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.type_voie (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 1.3000, '2026-11-16 11:10:00.477802+00', '2026-11-16 11:10:00.477802+00', 1, 0, 'BITUMEE', 'Bitume/Goudron') ON CONFLICT DO NOTHING;
INSERT INTO public.type_voie (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 1.4000, '2026-11-16 11:11:26.604171+00', '2026-11-16 11:11:26.604171+00', 2, 0, 'GRAND-AXE', 'Bordure de grand boulevard / Axe commercial') ON CONFLICT DO NOTHING;
INSERT INTO public.type_voie (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 0.9000, '2026-11-16 11:12:19.509561+00', '2026-11-16 11:12:19.509561+00', 3, 0, 'PISTE', 'Voie non lotie / Piste difficile d''accès') ON CONFLICT DO NOTHING;
INSERT INTO public.type_voie (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 1.0000, '2026-11-16 14:33:25.129444+00', '2026-11-16 14:33:25.129444+00', 4, 0, 'LATTERITE', 'Voie normale (Latérite / Sable carrossable)') ON CONFLICT DO NOTHING;


--
-- Data for Name: viabilisation; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.viabilisation (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 1.0500, '2026-11-16 11:20:43.344357+00', '2026-11-16 11:20:43.344357+00', 1, 0, 'ELECTRICITE', 'Electricité') ON CONFLICT DO NOTHING;
INSERT INTO public.viabilisation (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 1.0500, '2026-11-16 11:20:56.686603+00', '2026-11-16 11:20:56.687601+00', 2, 0, 'EAU', 'Eau') ON CONFLICT DO NOTHING;
INSERT INTO public.viabilisation (actif, coefficient, date_creation, date_modification, id, version, code, libelle) VALUES (true, 1.1000, '2026-11-16 11:24:23.978795+00', '2026-11-16 11:24:23.978795+00', 3, 0, 'RENFORCEE', 'Réseaux + Caniveaux/Assainissement') ON CONFLICT DO NOTHING;


--
-- Name: parametre_global_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.parametre_global_id_seq', 34, true);


--
-- Name: ponderation_methode_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.ponderation_methode_id_seq', 12, true);


--
-- Name: quartier_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.quartier_id_seq', 29, true);


--
-- Name: standing_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.standing_id_seq', 3, true);


--
-- Name: statut_juridique_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.statut_juridique_id_seq', 3, true);


--
-- Name: topographie_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.topographie_id_seq', 4, true);


--
-- Name: type_bien_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.type_bien_id_seq', 3, true);


--
-- Name: type_equipement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.type_equipement_id_seq', 3, true);


--
-- Name: type_voie_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.type_voie_id_seq', 4, true);


--
-- Name: viabilisation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.viabilisation_id_seq', 3, true);

SELECT setval(pg_get_serial_sequence('type_bien','id'),          COALESCE((SELECT MAX(id) FROM type_bien), 1));
SELECT setval(pg_get_serial_sequence('standing','id'),           COALESCE((SELECT MAX(id) FROM standing), 1));
SELECT setval(pg_get_serial_sequence('statut_juridique','id'),   COALESCE((SELECT MAX(id) FROM statut_juridique), 1));
SELECT setval(pg_get_serial_sequence('topographie','id'),        COALESCE((SELECT MAX(id) FROM topographie), 1));
SELECT setval(pg_get_serial_sequence('viabilisation','id'),      COALESCE((SELECT MAX(id) FROM viabilisation), 1));
SELECT setval(pg_get_serial_sequence('type_equipement','id'),    COALESCE((SELECT MAX(id) FROM type_equipement), 1));
SELECT setval(pg_get_serial_sequence('type_voie','id'),          COALESCE((SELECT MAX(id) FROM type_voie), 1));
SELECT setval(pg_get_serial_sequence('quartier','id'),           COALESCE((SELECT MAX(id) FROM quartier), 1));
SELECT setval(pg_get_serial_sequence('parametre_global','id'),   COALESCE((SELECT MAX(id) FROM parametre_global), 1));
SELECT setval(pg_get_serial_sequence('ponderation_methode','id'),COALESCE((SELECT MAX(id) FROM ponderation_methode), 1));

--
-- PostgreSQL database dump complete
--

