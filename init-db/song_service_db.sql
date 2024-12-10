CREATE DATABASE song_service_db;

\c song_service_db;

CREATE TABLE song
{
       id   SERIAL PRIMARY KEY,
       name CHARACTER VARYING(255),
       artist CHARACTER VARYING(255),
       album CHARACTER VARYING(255),
       length CHARACTER VARYING(255),
       resource_id BIGINT,
       year CHARACTER VARYING(255)
};
