-- Ganadi Database Initialization Script

-- Create database if not exists (already created by POSTGRES_DB)
-- CREATE DATABASE IF NOT EXISTS ganadi;

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create a user for the application (optional, using postgres user for simplicity)
-- CREATE USER ganadi_app WITH PASSWORD 'ganadi_app_password';
-- GRANT ALL PRIVILEGES ON DATABASE ganadi TO ganadi_app;

-- Set timezone
SET timezone = 'Asia/Seoul';

-- Create initial data (optional)
-- This will be populated by JPA/Hibernate with ddl-auto: update

-- Log initialization
\echo 'Ganadi database initialized successfully!'
