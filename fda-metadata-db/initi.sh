#!/bin/bash
set -e
export PGPASSWORD=$POSTGRES_PASSWORD;
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  --CREATE USER $APP_DB_USER WITH PASSWORD '$APP_DB_PASS';
  --CREATE DATABASE $fda_mdb;
  --GRANT ALL PRIVILEGES ON DATABASE $APP_DB_NAME TO $APP_DB_USER;
  --\connect $APP_DB_NAME $APP_DB_USER
  CREATE USER root;
  CREATE DATABASE root;
  BEGIN;
	CREATE TYPE gender AS ENUM ('F', 'M', 'T');
	CREATE TYPE accesstype AS ENUM ('R', 'W');
	CREATE TYPE image_environment_type AS ENUM ('USERNAME', 'PASSWORD', 'DATABASE', 'OTHER');

	CREATE CAST (character varying AS image_environment_type) WITH INOUT AS ASSIGNMENT;

	CREATE SEQUENCE public.mdb_image_environment_item_seq
	    START WITH 1
	    INCREMENT BY 1
	    NO MINVALUE
	    NO MAXVALUE
	    CACHE 1;

	CREATE TABLE public.mdb_image_environment_item (
	    id bigint NOT NULL DEFAULT nextval('mdb_image_environment_item_seq'),
		  created timestamp without time zone NOT NULL DEFAULT NOW(),
	    last_modified timestamp without time zone,
	    key character varying(255) NOT NULL,
	    value character varying(255) NOT NULL,
	    etype image_environment_type NOT NULL
	);

	CREATE SEQUENCE public.mdb_image_seq
	    START WITH 1
	    INCREMENT BY 1
	    NO MINVALUE
	    NO MAXVALUE
	    CACHE 1;

	CREATE TABLE public.mdb_image (
		id bigint PRIMARY KEY DEFAULT nextval('mdb_image_seq'),
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		last_modified timestamp without time zone,
		compiled timestamp without time zone NOT NULL,
		default_port integer NOT NULL,
		logo TEXT NOT NULL,
		hash character varying(255) NOT NULL,
		dialect character varying(255) NOT NULL,
		driver_class character varying(255) NOT NULL,
		jdbc_method character varying(255),
		repository character varying(255) NOT NULL,
		size bigint NOT NULL,
		tag character varying(255) NOT NULL,
		UNIQUE(repository, tag)
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
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		HASH character varying(255) NOT NULL,
		INTERNAL_NAME character varying(255) NOT NULL,
		LAST_MODIFIED timestamp without time zone,
		deleted timestamp without time zone NULL,
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
		external_id VARCHAR(255) UNIQUE NOT NULL,
		OID bigint UNIQUE NULL,
		First_name VARCHAR(50) NULL,
		Last_name VARCHAR(50) NULL,
		Gender gender,
		Preceding_titles VARCHAR(50),
		Postpositioned_title VARCHAR(50),
		Main_Email TEXT,
		created timestamp without time zone NOT NULL DEFAULT NOW(),
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

  CREATE SEQUENCE public.mdb_columns_enum_seq
		START WITH 1
		INCREMENT BY 1
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

	CREATE TABLE IF NOT EXISTS mdb_DATABASES (
		ID bigint PRIMARY KEY DEFAULT nextval('mdb_databases_seq'),
		container_id bigint REFERENCES mdb_CONTAINER(id),
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		deleted timestamp without time zone NULL,
		name character varying(255) NOT NULL,
		internal_name character varying(255) NOT NULL,
		exchange character varying(255) NOT NULL,
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
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		internal_name character varying(255) NOT NULL,
		topic character varying(255) NOT NULL,
		last_modified timestamp without time zone,
		tName VARCHAR(50),
		tDescription TEXT,
		NumCols INTEGER,
		NumRows INTEGER, 
		separator CHAR(1),
		element_null VARCHAR(50),
		skip_headers BOOLEAN,
		element_true VARCHAR(50),
		element_false VARCHAR(50),
		Version TEXT,
		PRIMARY KEY(tDBID,ID)
	);

	CREATE TABLE IF NOT EXISTS mdb_COLUMNS ( 
		ID bigint DEFAULT nextval('mdb_columns_seq'), 
		cDBID bigint, 
		tID bigint, 
		cName VARCHAR(100),
    internal_name VARCHAR(100) NOT NULL,
		Datatype VARCHAR(50), 
		ordinal_position INTEGER,
		is_primary_key BOOLEAN,
		is_unique BOOLEAN,
		auto_generated BOOLEAN DEFAULT false,
		date_format VARCHAR(255),
		is_null_allowed BOOLEAN,
		foreign_key VARCHAR(255),
		reference_table VARCHAR(255),
		check_expression character varying(255),
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		last_modified timestamp without time zone,
		FOREIGN KEY (cDBID,tID) REFERENCES mdb_TABLES(tDBID,ID), 
		PRIMARY KEY(cDBID, tID, ID)
	);

	CREATE TABLE IF NOT EXISTS mdb_COLUMNS_ENUMS (
		ID bigint DEFAULT nextval('mdb_columns_enum_seq'),
		eDBID bigint,
		tID bigint,
		cID bigint,
		enum_values CHARACTER VARYING(255) NOT NULL,
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		last_modified timestamp without time zone,
		FOREIGN KEY (eDBID, tID, cID) REFERENCES mdb_COLUMNS(cDBID, tID, ID),
		PRIMARY KEY (ID)
	);

	CREATE TABLE IF NOT EXISTS mdb_COLUMNS_nom ( 
		cDBID bigint, 
		tID bigint, 
		cID bigint, 
		maxlength INTEGER,
		last_modified timestamp without time zone,
		created timestamp without time zone NOT NULL DEFAULT NOW(),
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
		created timestamp without time zone NOT NULL DEFAULT NOW(),
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
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		FOREIGN KEY (cDBID,tID, cID) REFERENCES mdb_COLUMNS(cDBID,tID,ID),
		PRIMARY KEY(cDBID, tID, cID)
	);

	CREATE TABLE IF NOT EXISTS mdb_concepts (
		URI TEXT,
		name TEXT,
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		PRIMARY KEY(URI)
	);

	CREATE TABLE IF NOT EXISTS mdb_columns_concepts (
		cDBID bigint,
		tID bigint,
		cID bigint,
		URI TEXT REFERENCES mdb_concepts(URI),
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		FOREIGN KEY (cDBID,tID, cID) REFERENCES mdb_COLUMNS(cDBID,tID,ID),
		PRIMARY KEY(cDBID,tID,cID)
	);

	CREATE SEQUENCE public.mdb_view_seq
	    START WITH 1
	    INCREMENT BY 1
	    NO MINVALUE
	    NO MAXVALUE
	    CACHE 1;

	CREATE SEQUENCE public.mdb_citation_seq
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
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		InitialView BOOLEAN
	);

	CREATE TABLE IF NOT EXISTS mdb_CITATION (
		id bigint PRIMARY KEY DEFAULT nextval('mdb_citation_seq'),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    doi VARCHAR(255),
    query TEXT NOT NULL,
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		last_modified timestamp without time zone NOT NULL,
		deleted timestamp without time zone
	);
	
	CREATE TABLE IF NOT EXISTS mdb_views_databases( 
		mdb_view_id bigint REFERENCES mdb_VIEW(id), 
		databases_id bigint REFERENCES mdb_DATABASES(id),
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		PRIMARY KEY (mdb_view_id, databases_id)
	);

	CREATE TABLE IF NOT EXISTS mdb_feed ( 
		fDBID bigint, 
		fID bigint,
		fUserId INTEGER REFERENCES mdb_USERS(UserID), 
		fDataID INTEGER REFERENCES mdb_DATA(ID),
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		FOREIGN KEY (fDBID,fID) REFERENCES mdb_TABLES(tDBID,ID), 
		PRIMARY KEY (fDBID,fID,fUserId, fDataID)
	);

	CREATE TABLE IF NOT EXISTS mdb_update ( 
		uUserID INTEGER REFERENCES mdb_USERS(UserID),
		uDBID bigint REFERENCES mdb_DATABASES(id),
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		PRIMARY KEY (uUserID,uDBID)
	); 

	CREATE TABLE IF NOT EXISTS mdb_access (
		aUserID INTEGER REFERENCES mdb_USERS(UserID),
		aDBID bigint REFERENCES mdb_DATABASES(id),
		attime TIMESTAMP, 
		download BOOLEAN,
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		PRIMARY KEY (aUserID, aDBID)
	);

	CREATE TABLE IF NOT EXISTS mdb_have_access (
		hUserID INTEGER REFERENCES mdb_USERS(UserID),
		hDBID bigint REFERENCES mdb_DATABASES(id),
		hType accesstype,
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		PRIMARY KEY (hUserID,hDBID)
	);
	
	CREATE TABLE IF NOT EXISTS mdb_owns (
		oUserID INTEGER REFERENCES mdb_USERS(UserID),
		oDBID bigint REFERENCES mdb_DATABASES(ID),
		created timestamp without time zone NOT NULL DEFAULT NOW(),
		PRIMARY KEY (oUserID,oDBID)
	);

  COMMIT;
EOSQL
