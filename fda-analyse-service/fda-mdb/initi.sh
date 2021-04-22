#!/bin/bash
set -e
export PGPASSWORD=$POSTGRES_PASSWORD;
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  --CREATE USER $APP_DB_USER WITH PASSWORD '$APP_DB_PASS';
  --CREATE DATABASE $fda_mdb;
  --GRANT ALL PRIVILEGES ON DATABASE $APP_DB_NAME TO $APP_DB_USER;
  --\connect $APP_DB_NAME $APP_DB_USER
  BEGIN;
	CREATE SEQUENCE public.mdb_container_seq
		START WITH 1
		INCREMENT BY 1
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;
		
	CREATE SEQUENCE public.mdb_data_seq
		START WITH 1
		INCREMENT BY 1
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;
		
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
	
	CREATE SEQUENCE public.mdb_view_seq
	    START WITH 1
	    INCREMENT BY 1
	    NO MINVALUE
	    NO MAXVALUE
	    CACHE 1;
	    
	CREATE SEQUENCE public.mdb_image_seq
	    START WITH 1
	    INCREMENT BY 1
	    NO MINVALUE
	    NO MAXVALUE
	    CACHE 1;
	
	CREATE TYPE gender AS ENUM ('F', 'M', 'T');
	CREATE TYPE accesstype AS ENUM ('R', 'W');
	
	CREATE TABLE IF NOT EXISTS mdb_CONTAINER ( 
		id bigint PRIMARY KEY DEFAULT nextval('mdb_container_seq'),
		container_created timestamp without time zone NOT NULL,
		created timestamp without time zone NOT NULL,
		hash character varying(255) NOT NULL,
		internal_name character varying(255) NOT NULL,
		last_modified timestamp without time zone,
		name character varying(255) NOT NULL,
		port integer,
		image_id bigint,
		ip_address character varying(255)
	);

	CREATE TABLE IF NOT EXISTS mdb_DATA ( 
		ID INTEGER PRIMARY KEY DEFAULT nextval('mdb_data_seq'), 
		PROVENANCE TEXT, 
		FileEncoding TEXT, 
		FileType VARCHAR(100),
		Version TEXT,
		Seperator TEXT
	);  

	CREATE TABLE IF NOT EXISTS mdb_USERS ( 
		UserID INTEGER PRIMARY KEY,
		TISS_ID INTEGER,
		OID INTEGER,
		First_name VARCHAR(50),
		Last_name VARCHAR(50),
		Gender gender,
		Preceding_titles VARCHAR(50),
		Postpositioned_title VARCHAR(50),
		Main_Email TEXT
	); 

	CREATE TABLE IF NOT EXISTS mdb_CONTACTPERSON (
		cUserID INTEGER PRIMARY KEY REFERENCES mdb_USERS(UserID),
		Email TEXT
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
		Contact INTEGER REFERENCES mdb_CONTACTPERSON(cUserID),
		last_modified timestamp without time zone
	); 

	CREATE TABLE IF NOT EXISTS mdb_TABLES ( 
		id bigint NOT NULL DEFAULT nextval('mdb_tables_seq'),
		tDBID bigint REFERENCES mdb_DATABASES(id), 
		created timestamp without time zone NOT NULL, 
		internal_name character varying(255) NOT NULL, 
		last_modified timestamp without time zone, 
		tName VARCHAR(50), 
		NumCols INTEGER, 
		NumRows INTEGER, 
		Version TEXT,
		PRIMARY KEY(tDBID,tName)
	);

	CREATE TABLE IF NOT EXISTS mdb_COLUMNS ( 
		id bigint DEFAULT nextval('mdb_columns_seq'), 
		cDBID bigint NOT NULL, 
		tName VARCHAR(50) NOT NULL, 
		cName VARCHAR(50), 
		Datatype VARCHAR(50), 
		check_expression character varying(255),
		last_modified timestamp without time zone,
		FOREIGN KEY (cDBID,tName) REFERENCES mdb_TABLES(tDBID,tName), 
		PRIMARY KEY(cDBID, tName, cName)
	);

	CREATE TABLE IF NOT EXISTS mdb_nomCOLUMNS ( 
		cDBID bigint NOT NULL, 
		tName VARCHAR(50) NOT NULL, 
		cName VARCHAR(50), 
		maxlength INTEGER,
		FOREIGN KEY (cDBID,tName, cName) REFERENCES mdb_COLUMNS(cDBID,tName, cName), 
		PRIMARY KEY(cDBID, tName, cName)
	);

	CREATE TABLE IF NOT EXISTS mdb_numCOLUMNS ( 
		cDBID bigint NOT NULL, 
		tName VARCHAR(50) NOT NULL, 
		cName VARCHAR(50), 
		SIunit TEXT, 
		MaxVal NUMERIC, 
		MinVal NUMERIC , 
		Mean NUMERIC, 
		Median NUMERIC, 
		Sd Numeric, 
		Histogram INTEGER[][],
		last_update TIMESTAMP, 
		FOREIGN KEY (cDBID,tName, cName) REFERENCES mdb_COLUMNS(cDBID,tName,cName),
		PRIMARY KEY(cDBID, tName, cName)
	);

	CREATE TABLE IF NOT EXISTS mdb_catCOLUMNS ( 
		cDBID bigint NOT NULL, 
		tName VARCHAR(50) NOT NULL, 
		cName VARCHAR(50), 
		num_cat INTEGER, 
		cat_array TEXT[],
		FOREIGN KEY (cDBID,tName, cName) REFERENCES mdb_COLUMNS(cDBID,tName,cName),
		PRIMARY KEY(cDBID, tName, cName)
	);


	CREATE TABLE IF NOT EXISTS mdb_VIEW ( 
		id bigint PRIMARY KEY DEFAULT nextval('mdb_view_seq'),
		vName VARCHAR(50), 
		Query TEXT, 
		Public BOOLEAN , 
		NumCols INTEGER, 
		NumRows INTEGER
	);
	
	CREATE TABLE IF NOT EXISTS mdb_views_databases( 
		mdb_view_id bigint REFERENCES mdb_VIEW(id), 
		databases_id bigint REFERENCES mdb_DATABASES(id), 
		PRIMARY KEY (mdb_view_id, databases_id)
	);

	CREATE TABLE IF NOT EXISTS mdb_feed ( 
		fDBID bigint, 
		fName VARCHAR(50),
		fUserId INTEGER REFERENCES mdb_USERS(UserID), 
		fDataID INTEGER REFERENCES mdb_DATA(ID), 
		FOREIGN KEY (fDBID,fName) REFERENCES mdb_TABLES(tDBID,tNAME), 
		PRIMARY KEY (fDBID,fName,fUserId, fDataID)
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

  COMMIT;
EOSQL

