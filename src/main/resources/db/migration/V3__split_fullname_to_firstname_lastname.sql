-- Yarago Hospital ERP - Split full_name into first_name and last_name
-- Created: 2025-01-28
-- Description: Migrate from single full_name column to separate first_name and last_name columns

-- Step 1: Add new columns (nullable initially to allow data migration)
ALTER TABLE users ADD COLUMN first_name VARCHAR(500);
ALTER TABLE users ADD COLUMN last_name VARCHAR(500);

-- Step 2: Migrate existing data
-- Split full_name into first_name and last_name
-- For names with multiple parts, take first word as first_name and rest as last_name
UPDATE users
SET first_name = CASE
    WHEN POSITION(' ' IN full_name) > 0 THEN
        SUBSTRING(full_name FROM 1 FOR POSITION(' ' IN full_name) - 1)
    ELSE
        full_name
END,
last_name = CASE
    WHEN POSITION(' ' IN full_name) > 0 THEN
        TRIM(SUBSTRING(full_name FROM POSITION(' ' IN full_name) + 1))
    ELSE
        '' -- Empty string if no last name
END
WHERE full_name IS NOT NULL;

-- Step 3: Handle NULL values (set empty strings for users without names)
UPDATE users
SET first_name = COALESCE(first_name, ''),
    last_name = COALESCE(last_name, '')
WHERE first_name IS NULL OR last_name IS NULL;

-- Step 4: Drop the old full_name column
ALTER TABLE users DROP COLUMN full_name;

-- Step 5: Create indexes for the new columns (for search performance)
CREATE INDEX idx_users_first_name ON users(first_name);
CREATE INDEX idx_users_last_name ON users(last_name);

-- Add comment for documentation
COMMENT ON COLUMN users.first_name IS 'User first name - Encrypted';
COMMENT ON COLUMN users.last_name IS 'User last name - Encrypted';
