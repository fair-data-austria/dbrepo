from psycopg2 import connect 
import requests
import json 
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT

# Connect to PostgreSQL DBMS
con = connect(
        user="postgres",
        host="localhost", #todo change localhost to fda-metadata-db
        port=5432,
        #host = "fda-metadata-db",
        password="postgres")
con.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT);

# Obtain a DB Cursor
cursor = con.cursor();
name_Database = "fda_metadatabase";

# Create statement
sqlCreateDatabase = "create database "+name_Database+";"
cursor.execute(sqlCreateDatabase);

conn=connect(
    dbname="fda_metadatabase", 
    user = "postgres",
    host = "localhost", 
    port = 5432, 
    #host = "fda-metadata-db",
    password = "postgres"
)

cursor = conn.cursor() 

cursor.execute(f"""	CREATE TYPE gender AS ENUM ('F', 'M', 'T');
	CREATE TYPE accesstype AS ENUM ('R', 'W');
	
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
		Datatype VARCHAR(50), 
		ordinal_position INTEGER,
		check_expression character varying(255),
		FOREIGN KEY (cDBID,tID) REFERENCES mdb_TABLES(tDBID,ID), 
		PRIMARY KEY(cDBID, tID, ID)
	);

	CREATE TABLE IF NOT EXISTS mdb_nomCOLUMNS ( 
		cDBID bigint, 
		tID bigint, 
		cID bigint, 
		maxlength INTEGER,
		last_modified timestamp without time zone,
		FOREIGN KEY (cDBID,tID, cID) REFERENCES mdb_COLUMNS(cDBID,tID, ID), 
		PRIMARY KEY(cDBID, tID, cID)
	);

	CREATE TABLE IF NOT EXISTS mdb_numCOLUMNS ( 
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

	CREATE TABLE IF NOT EXISTS mdb_catCOLUMNS ( 
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
	);""")

conn.commit()
conn.close()
