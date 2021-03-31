CREATE EXTENSION temporal_tables;

CREATE TABLE query_store (
	pid SERIAL PRIMARY KEY,
	exec_timestamp timestamp,
	query varchar(255),
	re_written_query varchar(255),
	query_hash varchar(255),
	resultset_hash varchar(255)
);
