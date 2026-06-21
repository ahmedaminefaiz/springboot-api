-- V17__insert_citizens_and_alerts.sql
-- 10 citoyens de test + 3 alertes chacun (mot de passe : password)

-- ========== CITOYENS ==========

INSERT INTO users (nom, prenom, date_naissance, ville, email, password, phone, role, status, phone_verified)
VALUES
    ('Benali',    'Ahmed',   '1998-03-15', 'Casablanca', 'ahmed.benali@urbanalert.ma',    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000010', 'CITOYEN', 'ACTIVE', true),
    ('Tazi',      'Fatima',  '2000-07-22', 'Rabat',      'fatima.tazi@urbanalert.ma',     '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000011', 'CITOYEN', 'ACTIVE', true),
    ('Cherkaoui', 'Yassine', '1995-11-08', 'Marrakech',  'yassine.cherkaoui@urbanalert.ma','$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000012', 'CITOYEN', 'ACTIVE', true),
    ('Ouali',     'Nadia',   '1993-04-30', 'Fès',        'nadia.ouali@urbanalert.ma',     '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000013', 'CITOYEN', 'ACTIVE', true),
    ('Mrani',     'Khalid',  '2001-01-17', 'Tanger',     'khalid.mrani@urbanalert.ma',    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000014', 'CITOYEN', 'ACTIVE', true),
    ('Hassani',   'Salma',   '1997-09-05', 'Agadir',     'salma.hassani@urbanalert.ma',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000015', 'CITOYEN', 'ACTIVE', true),
    ('Benhaddou', 'Omar',    '1999-06-12', 'Meknès',     'omar.benhaddou@urbanalert.ma',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000016', 'CITOYEN', 'ACTIVE', true),
    ('Rochdi',    'Imane',   '1996-12-28', 'Oujda',      'imane.rochdi@urbanalert.ma',    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000017', 'CITOYEN', 'ACTIVE', true),
    ('Filali',    'Anas',    '2002-02-14', 'Kénitra',    'anas.filali@urbanalert.ma',     '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000018', 'CITOYEN', 'ACTIVE', true),
    ('Amrani',    'Houda',   '1994-08-19', 'Tétouan',    'houda.amrani@urbanalert.ma',    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000019', 'CITOYEN', 'ACTIVE', true)
ON CONFLICT (phone) DO NOTHING;

-- ========== ALERTES ==========
-- 3 alertes par citoyen, catégories alternées entre les 2 existantes

-- Ahmed Benali (+212600000010) — Casablanca
INSERT INTO alerts (title, description, latitude, longitude, address, status, priority, is_anonymous, user_id, category_id, created_at, updated_at)
VALUES
    ('Lampadaire éteint rue Hassan II',
     'Le lampadaire situé au coin de la rue Hassan II est éteint depuis 3 jours, créant une zone sombre dangereuse la nuit.',
     33.5731, -7.5898, 'Rue Hassan II, Casablanca', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000010'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW()),

    ('Poubelles non vidées depuis une semaine',
     'Les bacs à ordures du quartier Maârif n ont pas été collectés depuis plus de 7 jours. Odeurs nauséabondes.',
     33.5800, -7.6100, 'Quartier Maârif, Casablanca', 'NEW', 'MEDIUM', false,
     (SELECT id FROM users WHERE phone = '+212600000010'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW()),

    ('Panne d éclairage parking municipal',
     'Le parking municipal de la place Mohammed V est plongé dans l obscurité depuis hier soir.',
     33.5950, -7.6190, 'Place Mohammed V, Casablanca', 'NEW', 'MEDIUM', false,
     (SELECT id FROM users WHERE phone = '+212600000010'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW());

-- Fatima Tazi (+212600000011) — Rabat
INSERT INTO alerts (title, description, latitude, longitude, address, status, priority, is_anonymous, user_id, category_id, created_at, updated_at)
VALUES
    ('Déchets entassés près du marché central',
     'Des déchets s accumulent en dehors des bacs devant le marché central de Rabat. Risque sanitaire.',
     34.0209, -6.8416, 'Marché Central, Rabat', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000011'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW()),

    ('Éclairage défaillant avenue Fal Ould Oumeir',
     'Plusieurs lampadaires sont hors service sur toute la longueur de l avenue, dangereuse la nuit.',
     34.0150, -6.8300, 'Avenue Fal Ould Oumeir, Rabat', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000011'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW()),

    ('Sacs poubelles déchirés sur le trottoir',
     'Des sacs poubelles sont éparpillés sur le trottoir de la rue Soekarno suite au passage de chats errants.',
     34.0250, -6.8450, 'Rue Soekarno, Rabat', 'NEW', 'LOW', true,
     (SELECT id FROM users WHERE phone = '+212600000011'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW());

-- Yassine Cherkaoui (+212600000012) — Marrakech
INSERT INTO alerts (title, description, latitude, longitude, address, status, priority, is_anonymous, user_id, category_id, created_at, updated_at)
VALUES
    ('Lampe cassée place Jemaâ el-Fna',
     'Une lampe publique est cassée à l entrée de la place, le secteur est mal éclairé pour les touristes.',
     31.6295, -7.9811, 'Place Jemaâ el-Fna, Marrakech', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000012'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW()),

    ('Ordures non collectées quartier Guéliz',
     'Le quartier Guéliz souffre d une accumulation de déchets depuis plusieurs jours.',
     31.6340, -8.0100, 'Quartier Guéliz, Marrakech', 'NEW', 'MEDIUM', false,
     (SELECT id FROM users WHERE phone = '+212600000012'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW()),

    ('Rue sombre dans la médina',
     'Une ruelle dans la médina est totalement sans éclairage, dangereux pour les résidents la nuit.',
     31.6280, -7.9870, 'Médina, Marrakech', 'NEW', 'HIGH', true,
     (SELECT id FROM users WHERE phone = '+212600000012'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW());

-- Nadia Ouali (+212600000013) — Fès
INSERT INTO alerts (title, description, latitude, longitude, address, status, priority, is_anonymous, user_id, category_id, created_at, updated_at)
VALUES
    ('Dépôt sauvage rue Serrajine',
     'Un dépôt sauvage de déchets de construction bloque une partie du trottoir rue Serrajine.',
     34.0620, -4.9800, 'Rue Serrajine, Fès', 'NEW', 'MEDIUM', false,
     (SELECT id FROM users WHERE phone = '+212600000013'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW()),

    ('Panne éclairage boulevard Mohammed V',
     'Trois lampadaires consécutifs sont en panne sur le boulevard Mohammed V à Fès.',
     34.0400, -4.9990, 'Boulevard Mohammed V, Fès', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000013'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW()),

    ('Bacs saturés quartier Narjiss',
     'Les bacs à ordures du quartier Narjiss débordent et les ordures jonchent le sol.',
     34.0500, -4.9700, 'Quartier Narjiss, Fès', 'NEW', 'MEDIUM', true,
     (SELECT id FROM users WHERE phone = '+212600000013'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW());

-- Khalid Mrani (+212600000014) — Tanger
INSERT INTO alerts (title, description, latitude, longitude, address, status, priority, is_anonymous, user_id, category_id, created_at, updated_at)
VALUES
    ('Éclairage éteint corniche de Tanger',
     'Plusieurs points lumineux de la corniche sont hors service, situation risquée pour les promeneurs.',
     35.7891, -5.8100, 'Corniche de Tanger', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000014'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW()),

    ('Ordures non ramassées avenue d Espagne',
     'Les bacs devant les restaurants de l avenue d Espagne n ont pas été vidés depuis 5 jours.',
     35.7940, -5.8120, 'Avenue d Espagne, Tanger', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000014'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW()),

    ('Lampadaire tombé par terre',
     'Un lampadaire a été renversé, probablement par le vent, et gît sur le trottoir rue Ibn Batouta.',
     35.7700, -5.8000, 'Rue Ibn Batouta, Tanger', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000014'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW());

-- Salma Hassani (+212600000015) — Agadir
INSERT INTO alerts (title, description, latitude, longitude, address, status, priority, is_anonymous, user_id, category_id, created_at, updated_at)
VALUES
    ('Déchets sur la plage d Agadir',
     'Des déchets plastiques et organiques s accumulent sur la plage près du secteur touristique.',
     30.4159, -9.6090, 'Plage d Agadir', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000015'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW()),

    ('Éclairage défaillant promenade du bord de mer',
     'La promenade le long de la mer est mal éclairée la nuit, plusieurs lampadaires sont en panne.',
     30.4200, -9.6050, 'Promenade bord de mer, Agadir', 'NEW', 'MEDIUM', false,
     (SELECT id FROM users WHERE phone = '+212600000015'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW()),

    ('Bennes débordantes marché inzegane',
     'Les bennes à ordures du marché d Inzegane débordent chaque matin sans collecte régulière.',
     30.3580, -9.5350, 'Marché Inzegane, Agadir', 'NEW', 'MEDIUM', true,
     (SELECT id FROM users WHERE phone = '+212600000015'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW());

-- Omar Benhaddou (+212600000016) — Meknès
INSERT INTO alerts (title, description, latitude, longitude, address, status, priority, is_anonymous, user_id, category_id, created_at, updated_at)
VALUES
    ('Lampadaires en panne place Lahdim',
     'La moitié des lampadaires de la place Lahdim sont éteints, affectant la sécurité des piétons.',
     33.8935, -5.5560, 'Place Lahdim, Meknès', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000016'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW()),

    ('Déchets abandonnés avenue des FAR',
     'Des sacs de déchets ont été abandonnés en dehors des zones de collecte sur l avenue des FAR.',
     33.8900, -5.5470, 'Avenue des FAR, Meknès', 'NEW', 'LOW', false,
     (SELECT id FROM users WHERE phone = '+212600000016'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW()),

    ('Zone obscure parking gare de Meknès',
     'Le parking devant la gare ferroviaire est complètement non éclairé la nuit.',
     33.8780, -5.5350, 'Gare de Meknès', 'NEW', 'MEDIUM', false,
     (SELECT id FROM users WHERE phone = '+212600000016'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW());

-- Imane Rochdi (+212600000017) — Oujda
INSERT INTO alerts (title, description, latitude, longitude, address, status, priority, is_anonymous, user_id, category_id, created_at, updated_at)
VALUES
    ('Ordures entassées médina d Oujda',
     'Des montagnes de déchets s accumulent à l entrée de la médina d Oujda depuis plusieurs jours.',
     34.6830, -1.9120, 'Médina d Oujda', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000017'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW()),

    ('Rue non éclairée quartier Lazaret',
     'Toute la rue principale du quartier Lazaret est dans l obscurité suite à une panne collective.',
     34.6900, -1.9000, 'Quartier Lazaret, Oujda', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000017'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW()),

    ('Dépôt sauvage terrain vague',
     'Un terrain vague à proximité du lycée Ibn Khaldoun est utilisé comme décharge sauvage.',
     34.6750, -1.9200, 'Quartier Sidi Maâfa, Oujda', 'NEW', 'MEDIUM', true,
     (SELECT id FROM users WHERE phone = '+212600000017'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW());

-- Anas Filali (+212600000018) — Kénitra
INSERT INTO alerts (title, description, latitude, longitude, address, status, priority, is_anonymous, user_id, category_id, created_at, updated_at)
VALUES
    ('Éclairage en panne avenue Mohammed Diouri',
     'Les lampadaires de l avenue Mohammed Diouri sont hors service sur une longueur de 500m.',
     34.2610, -6.5802, 'Avenue Mohammed Diouri, Kénitra', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000018'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW()),

    ('Bacs déversés boulevard Mohamed V',
     'Les bacs à ordures ont été renversés sur le boulevard et les déchets trainent sur la chaussée.',
     34.2650, -6.5780, 'Boulevard Mohamed V, Kénitra', 'NEW', 'MEDIUM', false,
     (SELECT id FROM users WHERE phone = '+212600000018'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW()),

    ('Lampe grillée carrefour principal',
     'La lampe du carrefour des 4 routes est grillée, créant un risque d accident la nuit.',
     34.2500, -6.5900, 'Carrefour 4 routes, Kénitra', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000018'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW());

-- Houda Amrani (+212600000019) — Tétouan
INSERT INTO alerts (title, description, latitude, longitude, address, status, priority, is_anonymous, user_id, category_id, created_at, updated_at)
VALUES
    ('Déchets devant école primaire',
     'Des ordures s accumulent devant l école primaire Ibn Rochd, risque sanitaire pour les enfants.',
     35.5785, -5.3684, 'École Ibn Rochd, Tétouan', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000019'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW()),

    ('Panne éclairage quartier Sania',
     'Le quartier Sania est plongé dans le noir depuis deux nuits, plusieurs lampadaires en panne.',
     35.5720, -5.3750, 'Quartier Sania, Tétouan', 'NEW', 'HIGH', false,
     (SELECT id FROM users WHERE phone = '+212600000019'),
     (SELECT id FROM problem_types WHERE name = 'Éclairage public en panne'),
     NOW(), NOW()),

    ('Poubelles non vidées marché hebdomadaire',
     'Après le marché hebdomadaire, les bacs n ont pas été vidés et les ordures débordent sur la voie publique.',
     35.5800, -5.3600, 'Marché hebdomadaire, Tétouan', 'NEW', 'MEDIUM', true,
     (SELECT id FROM users WHERE phone = '+212600000019'),
     (SELECT id FROM problem_types WHERE name = 'Ordures non ramassées'),
     NOW(), NOW());