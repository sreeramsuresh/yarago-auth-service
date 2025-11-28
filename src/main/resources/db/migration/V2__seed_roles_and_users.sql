-- Yarago Hospital ERP - Seed Data for Auth Service
-- Created: 2025-01-27
-- Description: Initial roles and user accounts

-- Insert Roles
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', 'System Administrator - Full access to all features'),
('ROLE_DOCTOR', 'Ophthalmologist/Consultant - Access to consultations, prescriptions, surgery management'),
('ROLE_OPTOMETRIST', 'Optometrist - Access to vision testing, refraction, preliminary examinations'),
('ROLE_RECEPTIONIST', 'Front Desk - Patient registration, appointment scheduling, token management'),
('ROLE_BILLING_STAFF', 'Billing Executive - Invoice generation, payment collection, billing reports'),
('ROLE_COUNSELOR', 'Surgery Counselor - Pre-surgery counseling, insurance coordination'),
('ROLE_PHARMACIST', 'Pharmacist - Medication dispensing, inventory management'),
('ROLE_LAB_TECHNICIAN', 'Lab Technician - Diagnostic tests, investigations'),
('ROLE_MANAGER', 'Branch Manager - Branch-level administration and reporting');

-- Insert Users (Password: Yarago@2025 for all users - BCrypt hashed)
-- Note: In production, these should be changed immediately
INSERT INTO users (
    username, email, password, first_name, last_name, phone_number,
    designation, department, employee_id, branch_id, active
) VALUES
(
    'admin',
    'admin@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'System',
    'Administrator',
    '+91-9876543210',
    'System Administrator',
    'IT & Administration',
    'EMP001',
    1,
    true
),
(
    'dr.sharma',
    'dr.sharma@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Rajesh',
    'Sharma',
    '+91-9876543211',
    'Senior Ophthalmologist',
    'Ophthalmology',
    'EMP002',
    1,
    true
),
(
    'dr.patel',
    'dr.patel@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Priya',
    'Patel',
    '+91-9876543212',
    'Consultant Ophthalmologist',
    'Ophthalmology',
    'EMP003',
    1,
    true
),
(
    'dr.kumar',
    'dr.kumar@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Anil',
    'Kumar',
    '+91-9876543213',
    'Pediatric Ophthalmologist',
    'Ophthalmology',
    'EMP004',
    1,
    true
),
(
    'opt.mehta',
    'opt.mehta@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Neha',
    'Mehta',
    '+91-9876543214',
    'Senior Optometrist',
    'Optometry',
    'EMP005',
    1,
    true
),
(
    'opt.singh',
    'opt.singh@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Rahul',
    'Singh',
    '+91-9876543215',
    'Optometrist',
    'Optometry',
    'EMP006',
    1,
    true
),
(
    'opt.verma',
    'opt.verma@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Pooja',
    'Verma',
    '+91-9876543216',
    'Optometrist',
    'Optometry',
    'EMP007',
    1,
    true
),
(
    'reception1',
    'reception1@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Anjali',
    'Desai',
    '+91-9876543217',
    'Front Desk Executive',
    'Reception',
    'EMP008',
    1,
    true
),
(
    'reception2',
    'reception2@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Sunita',
    'Rao',
    '+91-9876543218',
    'Front Desk Executive',
    'Reception',
    'EMP009',
    1,
    true
),
(
    'billing1',
    'billing1@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Ramesh',
    'Joshi',
    '+91-9876543219',
    'Billing Executive',
    'Billing',
    'EMP010',
    1,
    true
),
(
    'billing2',
    'billing2@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Kavita',
    'Nair',
    '+91-9876543220',
    'Senior Billing Executive',
    'Billing',
    'EMP011',
    1,
    true
),
(
    'counselor1',
    'counselor1@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Deepak',
    'Gupta',
    '+91-9876543221',
    'Surgery Counselor',
    'Counseling',
    'EMP012',
    1,
    true
),
(
    'pharmacist1',
    'pharmacist1@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Sanjay',
    'Pillai',
    '+91-9876543222',
    'Chief Pharmacist',
    'Pharmacy',
    'EMP013',
    1,
    true
),
(
    'lab1',
    'lab1@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Vinod',
    'Reddy',
    '+91-9876543223',
    'Lab Technician',
    'Laboratory',
    'EMP014',
    1,
    true
),
(
    'manager1',
    'manager1@yarago.com',
    '$2a$10$8K1p/a0dL3.rOXYk6VRfNOGGdVvON.7bXr4Z8X.rF3C7Xz8K1p/a0',
    'Suresh',
    'Iyer',
    '+91-9876543224',
    'Branch Manager',
    'Administration',
    'EMP015',
    1,
    true
);

-- Assign roles to users
-- Admin user - all roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin';

-- Doctors
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username IN ('dr.sharma', 'dr.patel', 'dr.kumar') AND r.name = 'ROLE_DOCTOR';

-- Optometrists
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username IN ('opt.mehta', 'opt.singh', 'opt.verma') AND r.name = 'ROLE_OPTOMETRIST';

-- Receptionists
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username IN ('reception1', 'reception2') AND r.name = 'ROLE_RECEPTIONIST';

-- Billing Staff
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username IN ('billing1', 'billing2') AND r.name = 'ROLE_BILLING_STAFF';

-- Counselor
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'counselor1' AND r.name = 'ROLE_COUNSELOR';

-- Pharmacist
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'pharmacist1' AND r.name = 'ROLE_PHARMACIST';

-- Lab Technician
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'lab1' AND r.name = 'ROLE_LAB_TECHNICIAN';

-- Manager
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'manager1' AND r.name = 'ROLE_MANAGER';
