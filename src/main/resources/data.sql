-- ==========================================================
-- SCRIPT D'INITIALISATION GÉNÉRALE (LIAS Lab)
-- Objectif : Événements, Publications et Archives Documentaires
-- ==========================================================

-- 1. INSERTION DES ÉVÉNEMENTS (2023 - 2026)
INSERT IGNORE INTO evenement (id, titre, description, date_evenement, lieu, type) VALUES
(3, 'Séminaire : Éthique et Big Data', 'Discussion sur les enjeux juridiques et moraux de la manipulation des données massives.', '2023-05-15', 'Salle de conférence FSBM', 'SEMINAIRE'),
(4, 'Workshop sur l''IA Générative', 'Atelier pratique sur les LLM et le prompt engineering pour la recherche.', '2023-11-20', 'Labo DSBD - Salle 4', 'WORKSHOP'),
(5, 'Conférence Internationale sur le Cloud Computing', 'Regroupement annuel des chercheurs sur les infrastructures distribuées.', '2024-03-10', 'Amphi Central - FSBM', 'CONFERENCE'),
(6, 'Webinaire : MLOps en Production', 'Session en ligne sur le cycle de vie des modèles d''apprentissage automatique.', '2024-06-25', 'Visioconférence Teams', 'SEMINAIRE'),
(7, 'Workshop : Analyse de Graphes Sociaux', 'Techniques avancées de Data Mining appliquées aux réseaux complexes.', '2024-09-15', 'Salle de réunion LIAS', 'WORKSHOP'),
(8, 'Conférence : Futur de la Cybersécurité', 'Impact de l''informatique quantique sur le cryptage actuel.', '2025-02-12', 'Palais des Congrès', 'CONFERENCE'),
(9, 'Séminaire : Santé Connectée & IoT', 'Applications de la Data Science dans le diagnostic médical préventif.', '2025-05-20', 'Visioconférence Zoom', 'SEMINAIRE'),
(10, 'Workshop : Vision par Ordinateur', 'Détection d''objets et segmentation d''images par Deep Learning.', '2025-10-05', 'Laboratoire LIAS', 'WORKSHOP'),
(11, 'Summit LIAS 2026', 'Bilan quinquennal du laboratoire et présentation des nouvelles thèses.', '2026-01-15', 'Grand Amphi - Casablanca', 'CONFERENCE'),
(12, 'Journée Doctorants 2026', 'Présentations rapides des travaux de recherche en cours.', '2026-06-10', 'Salle des Actes', 'SEMINAIRE');

-- 2. AFFECTATION DES ORGANISATEURS (Ahmed IDRISSI: 2, Driss ALAOUI: 1)
INSERT IGNORE INTO evenement_organisateurs (evenement_id, membre_id) VALUES
(3, 1), (3, 2), (4, 2), (5, 1), (6, 1), (6, 2), (7, 2), (8, 1), (9, 2), (10, 1), (11, 1), (11, 2), (12, 2);

-- 3. ARCHIVAGE DOCUMENTAIRE (Lien avec les Événements)
INSERT IGNORE INTO document (id, titre, chemin_fichier, type_document, date_upload, evenement_id) VALUES
(3, 'Programme détaillé - Éthique & Big Data', 'publications/test_publication.pdf', 'PROGRAMME', '2023-05-10 10:00:00', 3),
(4, 'Attestation de participation - Workshop IA', 'publications/test_publication.pdf', 'RAPPORT', '2023-11-21 16:30:00', 4),
(5, 'Livret des résumés - Cloud Computing 2024', 'publications/test_publication.pdf', 'PROGRAMME', '2024-03-05 09:00:00', 5),
(6, 'Présentation Slides - MLOps', 'publications/test_publication.pdf', 'RAPPORT', '2024-06-26 11:15:00', 6);

-- 4. PUBLICATIONS SCIENTIFIQUES
/*INSERT IGNORE INTO publication (id, titre, revue, date_publication, chemin_fichier, equipe_id) VALUES
(13, 'Deep Learning for Medical Imaging: A Survey', 'IEEE Access', '2023-02-15', 'publications/test_publication.pdf', 1),
(14, 'Transformers in NLP for Arabic Dialects', 'ACM Transactions', '2023-05-20', 'publications/test_publication.pdf', 1),
(15, 'Optimisation des réseaux de neurones par essaim', 'Journal of ML Research', '2023-11-10', 'publications/test_publication.pdf', 1),
(16, 'Vision par ordinateur pour le diagnostic agricole', 'Sensors Journal', '2024-03-05', 'publications/test_publication.pdf', 1),
(17, 'Generative AI in Academic Research: Ethical Views', 'Nature Machine Intelligence', '2024-08-12', 'publications/test_publication.pdf', 1),
(18, 'Reinforcement Learning for Autonomous Systems', 'Robotics and AI', '2025-01-20', 'publications/test_publication.pdf', 1),
(19, 'Big Data Analytics for Smart City Management', 'Data Science Journal', '2023-04-10', 'publications/test_publication.pdf', 2),
(20, 'Predictive Modeling in Financial Markets', 'Quantitative Finance', '2023-09-25', 'publications/test_publication.pdf', 2),
(21, 'Analyse de graphes massifs pour réseaux sociaux', 'Graph Theory Reviews', '2023-12-01', 'publications/test_publication.pdf', 2),
(22, 'Scalable Cloud Computing for Large Datasets', 'Journal of Cloud Computing', '2024-02-28', 'publications/test_publication.pdf', 2),
(23, 'Privacy-Preserving Data Mining Techniques', 'Security & Privacy', '2024-07-15', 'publications/test_publication.pdf', 2),
(24, 'Real-time Analytics in Edge Computing', 'IoT Journal', '2024-11-20', 'publications/test_publication.pdf', 2),
(25, 'Forecasting Energy Consumption using LSTM', 'Applied Energy', '2025-02-05', 'publications/test_publication.pdf', 2),
(26, 'Microservices Architecture: Best Practices', 'Software Engineering Journal', '2024-05-18', 'publications/test_publication.pdf', 6),
(27, 'DevOps adoption in Large Scale Projects', 'IEEE Software', '2025-03-10', 'publications/test_publication.pdf', 6);

-- 5. LIAISON PUBLICATIONS-AUTEURS (ManyToMany)
INSERT IGNORE INTO membre_publications (membre_id, publication_id) VALUES
(2, 13), (9, 14), (1, 15), (2, 16), (1, 17), (9, 18),
(3, 19), (11, 20), (1, 21), (3, 22), (11, 23), (3, 24), (1, 25),
(7, 26), (7, 27);*/
