#!/bin/bash
set -e
export PGPASSWORD=$POSTGRES_PASSWORD;
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  --CREATE USER $APP_DB_USER WITH PASSWORD '$APP_DB_PASS';
  --CREATE DATABASE $fda_mdb;
  --GRANT ALL PRIVILEGES ON DATABASE $APP_DB_NAME TO $APP_DB_USER;
  --\connect $APP_DB_NAME $APP_DB_USER
  BEGIN;
	CREATE TYPE gender AS ENUM ('F', 'M', 'T');
	CREATE TYPE accesstype AS ENUM ('R', 'W');
		
	CREATE SEQUENCE public.mdb_environment_item_seq
	    START WITH 1
	    INCREMENT BY 1
	    NO MINVALUE
	    NO MAXVALUE
	    CACHE 1;
	
	CREATE TABLE public.mdb_environment_item (
	    id bigint NOT NULL DEFAULT nextval('mdb_environment_item_seq'),
	    created timestamp without time zone NOT NULL,
	    last_modified timestamp without time zone,
	    key character varying(255) NOT NULL,
	    value character varying(255) NOT NULL
	);
	
	CREATE SEQUENCE public.mdb_image_seq
	    START WITH 1
	    INCREMENT BY 1
	    NO MINVALUE
	    NO MAXVALUE
	    CACHE 1;
	
	CREATE TABLE public.mdb_image (
		id bigint PRIMARY KEY DEFAULT nextval('mdb_image_seq'),
		created timestamp without time zone NOT NULL,
		last_modified timestamp without time zone,
		compiled timestamp without time zone NOT NULL,
		default_port integer NOT NULL,
		hash character varying(255) NOT NULL,
		repository character varying(255) NOT NULL,
		size bigint NOT NULL,
		tag character varying(255) NOT NULL
	);

	CREATE TABLE public.mdb_image_environment (
		container_image_id bigint NOT NULL,
		environment_id bigint NOT NULL
	);
	
	CREATE SEQUENCE public.mdb_container_seq
		START WITH 1
		INCREMENT BY 1
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

	CREATE SEQUENCE public.mdb_user_seq
		START WITH 1
		INCREMENT BY 1
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;
	
	CREATE TABLE IF NOT EXISTS mdb_CONTAINER ( 
		ID bigint PRIMARY KEY DEFAULT nextval('mdb_container_seq'),
		CONTAINER_CREATED timestamp without time zone NOT NULL,
		CREATED timestamp without time zone NOT NULL,
		HASH character varying(255) NOT NULL,
		INTERNAL_NAME character varying(255) NOT NULL,
		LAST_MODIFIED timestamp without time zone,
		NAME character varying(255) NOT NULL,
		PORT integer,
		IMAGE_ID bigint REFERENCES mdb_image(id),
		IP_ADDRESS character varying(255)
	);
	
	CREATE SEQUENCE public.mdb_data_seq
		START WITH 1
		INCREMENT BY 1
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

	CREATE TABLE IF NOT EXISTS mdb_DATA ( 
		ID bigint PRIMARY KEY DEFAULT nextval('mdb_data_seq'),
		PROVENANCE TEXT, 
		FileEncoding TEXT, 
		FileType VARCHAR(100),
		Version TEXT,
		Seperator TEXT
	);  

	CREATE TABLE IF NOT EXISTS mdb_USERS ( 
		UserID bigint PRIMARY KEY DEFAULT nextval('mdb_user_seq'),
		TISS_ID bigint,
		OID bigint,
		First_name VARCHAR(50),
		Last_name VARCHAR(50),
		Gender gender,
		Preceding_titles VARCHAR(50),
		Postpositioned_title VARCHAR(50),
		Main_Email TEXT,
		created timestamp without time zone NOT NULL,
		last_modified timestamp without time zone
	); 
	
	CREATE SEQUENCE public.mdb_databases_seq
		START WITH 1
		INCREMENT BY 1
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;
		
	CREATE SEQUENCE public.mdb_tables_seq
		START WITH 1
		INCREMENT BY 1
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;
		
	CREATE SEQUENCE public.mdb_columns_seq
		START WITH 1
		INCREMENT BY 1
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

  CREATE SEQUENCE public.mdb_queries_seq
		START WITH 1
		INCREMENT BY 1
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

	CREATE TABLE IF NOT EXISTS mdb_queries (
    ID bigint NOT NULL DEFAULT nextval('mdb_queries_seq'),
    execution_timestamp timestamp without time zone NOT NULL,
    query TEXT NOT NULL,
    query_normalized TEXT NOT NULL,
    query_hash character varying(255) NULL,
    result_hash character varying(255) NULL,
    result_number INTEGER NULL,
    created timestamp without time zone NOT NULL,
    last_modified timestamp without time zone,
    PRIMARY KEY(ID)
    );

	CREATE TABLE IF NOT EXISTS mdb_DATABASES ( 
		ID bigint PRIMARY KEY DEFAULT nextval('mdb_databases_seq'), 
		container_id bigint REFERENCES mdb_CONTAINER(id),
		created timestamp without time zone NOT NULL,
		name character varying(255) NOT NULL, 
		internal_name character varying(255) NOT NULL,
		ResourceType TEXT, 
		Description TEXT, 
		Engine VARCHAR(20) DEFAULT 'Postgres', 
		Publisher VARCHAR(50), 
		Year DATE DEFAULT CURRENT_DATE, 
		is_public BOOLEAN NOT NULL DEFAULT TRUE, 
		last_modified timestamp without time zone, 
		Creator INTEGER REFERENCES mdb_USERS(UserID), 
		Contactperson INTEGER REFERENCES mdb_USERS(UserID)
	); 

	CREATE TABLE IF NOT EXISTS mdb_TABLES ( 
		ID bigint NOT NULL DEFAULT nextval('mdb_tables_seq'),
		tDBID bigint REFERENCES mdb_DATABASES(id), 
		created timestamp without time zone NOT NULL, 
		internal_name character varying(255) NOT NULL, 
		last_modified timestamp without time zone, 
		tName VARCHAR(50), 
		NumCols INTEGER, 
		NumRows INTEGER, 
		Version TEXT,
		PRIMARY KEY(tDBID,ID)
	);

	CREATE TABLE IF NOT EXISTS mdb_COLUMNS ( 
		ID bigint DEFAULT nextval('mdb_columns_seq'), 
		cDBID bigint, 
		tID bigint, 
		cName VARCHAR(50),
    internal_name VARCHAR(50) NOT NULL,
		Datatype VARCHAR(50), 
		ordinal_position INTEGER,
		is_primary_key BOOLEAN,
		is_null_allowed BOOLEAN,
		foreign_key VARCHAR(50),
		check_expression character varying(255),
		created timestamp without time zone NOT NULL,
		last_modified timestamp without time zone,
		FOREIGN KEY (cDBID,tID) REFERENCES mdb_TABLES(tDBID,ID), 
		PRIMARY KEY(cDBID, tID, ID)
	);

	CREATE TABLE IF NOT EXISTS mdb_COLUMNS_nom ( 
		cDBID bigint, 
		tID bigint, 
		cID bigint, 
		maxlength INTEGER,
		last_modified timestamp without time zone,
		FOREIGN KEY (cDBID,tID, cID) REFERENCES mdb_COLUMNS(cDBID,tID, ID), 
		PRIMARY KEY(cDBID, tID, cID)
	);

	CREATE TABLE IF NOT EXISTS mdb_COLUMNS_num ( 
		cDBID bigint, 
		tID bigint, 
		cID bigint, 
		SIunit TEXT, 
		MaxVal NUMERIC, 
		MinVal NUMERIC , 
		Mean NUMERIC, 
		Median NUMERIC, 
		Sd Numeric, 
		Histogram INTEGER[],
		last_modified timestamp without time zone,
		FOREIGN KEY (cDBID,tID, cID) REFERENCES mdb_COLUMNS(cDBID,tID,ID),
		PRIMARY KEY(cDBID, tID, cID)
	);

	CREATE TABLE IF NOT EXISTS mdb_COLUMNS_cat ( 
		cDBID bigint, 
		tID bigint, 
		cID bigint, 
		num_cat INTEGER, 
		cat_array TEXT[],
		last_modified timestamp without time zone,
		FOREIGN KEY (cDBID,tID, cID) REFERENCES mdb_COLUMNS(cDBID,tID,ID),
		PRIMARY KEY(cDBID, tID, cID)
	);
	
	CREATE SEQUENCE public.mdb_view_seq
	    START WITH 1
	    INCREMENT BY 1
	    NO MINVALUE
	    NO MAXVALUE
	    CACHE 1;

	CREATE TABLE IF NOT EXISTS mdb_VIEW ( 
		id bigint PRIMARY KEY DEFAULT nextval('mdb_view_seq'),
		vName VARCHAR(50), 
		Query TEXT, 
		Public BOOLEAN , 
		NumCols INTEGER, 
		NumRows INTEGER, 
		InitialView BOOLEAN
	);
	
	CREATE TABLE IF NOT EXISTS mdb_views_databases( 
		mdb_view_id bigint REFERENCES mdb_VIEW(id), 
		databases_id bigint REFERENCES mdb_DATABASES(id), 
		PRIMARY KEY (mdb_view_id, databases_id)
	);

	CREATE TABLE IF NOT EXISTS mdb_feed ( 
		fDBID bigint, 
		fID bigint,
		fUserId INTEGER REFERENCES mdb_USERS(UserID), 
		fDataID INTEGER REFERENCES mdb_DATA(ID), 
		FOREIGN KEY (fDBID,fID) REFERENCES mdb_TABLES(tDBID,ID), 
		PRIMARY KEY (fDBID,fID,fUserId, fDataID)
	);

	CREATE TABLE IF NOT EXISTS mdb_update ( 
		uUserID INTEGER REFERENCES mdb_USERS(UserID),
		uDBID bigint REFERENCES mdb_DATABASES(id), 
		PRIMARY KEY (uUserID,uDBID)
	); 

	CREATE TABLE IF NOT EXISTS mdb_access (
		aUserID INTEGER REFERENCES mdb_USERS(UserID),
		aDBID bigint REFERENCES mdb_DATABASES(id),
		attime TIMESTAMP, 
		download BOOLEAN, 
		PRIMARY KEY (aUserID, aDBID)
	);

	CREATE TABLE IF NOT EXISTS mdb_have_access (
		hUserID INTEGER REFERENCES mdb_USERS(UserID),
		hDBID bigint REFERENCES mdb_DATABASES(id),
		hType accesstype,
		PRIMARY KEY (hUserID,hDBID)
	);
	
	CREATE TABLE IF NOT EXISTS mdb_owns (
		oUserID INTEGER REFERENCES mdb_USERS(UserID),
		oDBID bigint REFERENCES mdb_DATABASES(ID),
		PRIMARY KEY (oUserID,oDBID)
	);

  COMMIT;
EOSQL
