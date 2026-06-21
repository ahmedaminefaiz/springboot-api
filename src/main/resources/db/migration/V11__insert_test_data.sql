-- 1. Administrateur de test (mot de passe : password)
INSERT INTO users (nom, prenom, date_naissance, ville, email, password, phone, role, status, phone_verified)
VALUES ('Admin', 'Sami', '1990-01-01', 'Casablanca', 'admin@urbanalert.ma', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000001', 'ADMIN', 'ACTIVE', true)
ON CONFLICT (phone) DO NOTHING;

-- 2. Citoyen de test (mot de passe : password)
INSERT INTO users (nom, prenom, date_naissance, ville, email, password, phone, role, status, phone_verified)
VALUES ('Alaoui', 'Karim', '1995-05-15', 'Casablanca', 'karim.citoyen@urbanalert.ma', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000002', 'CITOYEN', 'ACTIVE', true)
ON CONFLICT (phone) DO NOTHING;

-- 3. Super Agent de test (mot de passe : password)
INSERT INTO users (nom, prenom, date_naissance, ville, email, password, phone, role, status, phone_verified)
VALUES ('Bennani', 'Sara', '1988-03-20', 'Casablanca', 'sara.superagent@urbanalert.ma', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000003', 'SUPER_AGENT', 'ACTIVE', true)
ON CONFLICT (phone) DO NOTHING;

-- 4. Agent de test (mot de passe : password)
INSERT INTO users (nom, prenom, date_naissance, ville, email, password, phone, role, status, phone_verified)
VALUES ('Idrissi', 'Youssef', '1992-07-10', 'Casablanca', 'youssef.agent@urbanalert.ma', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000004', 'AGENT', 'ACTIVE', true)
ON CONFLICT (phone) DO NOTHING;

-- 5. Insertion de catégories de test liées à l'administrateur
INSERT INTO problem_types (name, icon, admin_id)
SELECT 'Éclairage public en panne', 'lightbulb', id 
FROM users 
WHERE phone = '+212600000001'
ON CONFLICT (name) DO NOTHING;

INSERT INTO problem_types (name, icon, admin_id)
SELECT 'Ordures non ramassées', 'trash', id 
FROM users 
WHERE phone = '+212600000001'
ON CONFLICT (name) DO NOTHING;
