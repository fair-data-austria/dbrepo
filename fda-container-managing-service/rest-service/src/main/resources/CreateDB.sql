CREATE EXTENSION temporal_tables;

CREATE TABLE query_store (
	pid SERIAL PRIMARY KEY,
	exec_timestamp tstzrange,
	query varchar(255),
	table_name varchar(255),
	query_hash varchar(255),
	resultset_hash varchar(255)
);
