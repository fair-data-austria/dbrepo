CREATE EXTENSION temporal_tables;

CREATE TABLE query_store (
	pid SERIAL PRIMARY KEY,
	exec_timestamp timestamp,
	resourceName varchar(255),
	query varchar(255),
	query_hash varchar(255),
	resultset_hash varchar(255)
);
