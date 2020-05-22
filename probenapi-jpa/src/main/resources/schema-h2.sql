-- CREATE DATABASE IF NOT EXISTS: H2 is supposed to create the schema (with correct name) automatically but it doesnt!
-- wir muessen es machen!
CREATE SCHEMA IF NOT EXISTS @dbName@;