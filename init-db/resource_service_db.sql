CREATE DATABASE resource_service_db;

\c resource_service_db

CREATE TABLE resource
{
    id SERIAL PRIMARY KEY,
    file BYTEA NOT NULL
};