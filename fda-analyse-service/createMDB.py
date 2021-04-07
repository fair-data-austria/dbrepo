from psycopg2 import connect 
import requests
import json 

# Post request on FDA-Container-Managing Service
CONTAINER_MANAGING_URL = "http://localhost:9091/api/container/"
r = requests.post(CONTAINER_MANAGING_URL, json = {  "name": "Metadatabase", 
	"repository": "postgres",
	"tag": "latest"
})

r.json()

print(r.json())

s = requests.put(CONTAINER_MANAGING_URL+str(r.json()['id']), json={
  "action": "START"
})

print(s.json)

DATABASE_MANAGING_URL = "http://localhost:9092/api/database/"
t = requests.post(DATABASE_MANAGING_URL, json = {
  "containerId": 4,
  "name": "Metadatabase"
})

print(t.json)

conn=connect(
    dbname="fda", 
    user = "postgres",
    host = "fda-metadata-db",
    password = "postgres"
)

cursor = conn.cursor() 

cursor.execute(f"""CREATE SEQUENCE seq_data;
CREATE SEQUENCE seq_user; 
CREATE TYPE gender AS ENUM ('F', 'M', 'T');
CREATE TYPE accesstype AS ENUM ('R', 'W');

CREATE TABLE md_DATA ( 
	ID INTEGER PRIMARY KEY DEFAULT nextval('seq_data'), 
	PROVENANCE TEXT, 
	FileEncoding TEXT, 
	FileType VARCHAR(100),
	Version TEXT,
	Seperator TEXT
);  

CREATE TABLE md_USERS ( 
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

CREATE TABLE md_CONTACTPERSON (
	cUserID INTEGER PRIMARY KEY REFERENCES md_USERS(UserID),
	Email TEXT
);

CREATE TABLE md_DATABASES ( 
	DBID TEXT PRIMARY KEY, -- (= DockerContainer ID)
	Title VARCHAR(50), 
	ResourceType TEXT, 
	Description TEXT, 
	Engine VARCHAR(20) DEFAULT 'Postgres', 
	Publisher VARCHAR(50), 
	Year DATE DEFAULT CURRENT_DATE, 
	Open BOOLEAN DEFAULT TRUE, 
	Contact INTEGER REFERENCES md_CONTACTPERSON(cUserID)
); 

CREATE TABLE md_TABLES ( 
	tDBID TEXT REFERENCES md_DATABASES(DBID), 
	tName VARCHAR(50), 
	NumCols INTEGER, 
	NumRows INTEGER, 
	Version TEXT,
	PRIMARY KEY(tDBID,tName)
);

CREATE TABLE md_COLUMNS ( 
	cDBID TEXT NOT NULL, 
	tName VARCHAR(50) NOT NULL, 
	cName VARCHAR(50), 
	Datatype VARCHAR(50), 
	FOREIGN KEY (cDBID,tName) REFERENCES md_TABLES(tDBID,tName),
	PRIMARY KEY(cDBID, tName, cName)
);

CREATE TABLE md_nomCOLUMNS ( 
	cDBID TEXT NOT NULL, 
	tName VARCHAR(50) NOT NULL, 
	cName VARCHAR(50), 
	maxlength INTEGER,
	FOREIGN KEY (cDBID,tName, cName) REFERENCES md_COLUMNS(cDBID,tName, cName),
	PRIMARY KEY(cDBID, tName, cName)
);

CREATE TABLE md_numCOLUMNS ( 
	cDBID TEXT NOT NULL, 
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
	FOREIGN KEY (cDBID,tName, cName) REFERENCES md_COLUMNS(cDBID,tName,cName),
	PRIMARY KEY(cDBID, tName, cName)
);

CREATE TABLE md_catCOLUMNS ( 
	cDBID TEXT NOT NULL, 
	tName VARCHAR(50) NOT NULL, 
	cName VARCHAR(50), 
	num_cat INTEGER, 
	cat_array TEXT[],
	FOREIGN KEY (cDBID,tName, cName) REFERENCES md_COLUMNS(cDBID,tName,cName),
	PRIMARY KEY(cDBID, tName, cName)
);


CREATE TABLE md_VIEW ( 
	vDBID TEXT REFERENCES md_DATABASES(DBID), 
	vName VARCHAR(50), 
	Query TEXT, 
	Public BOOLEAN , 
	NumCols INTEGER, 
	NumRows INTEGER, 
	PRIMARY KEY (vDBID,vName)
);

CREATE TABLE md_feed ( 
	fDBID TEXT, 
	fName VARCHAR(50),
	fUserId INTEGER REFERENCES md_USERS(UserID), 
	fDataID INTEGER REFERENCES md_DATA(ID), 
	FOREIGN KEY (fDBID,fName) REFERENCES md_TABLES(tDBID,tNAME), 
	PRIMARY KEY (fDBID,fName,fUserId, fDataID)
);

CREATE TABLE md_update ( 
	uUserID INTEGER REFERENCES md_USERS(UserID),
	uDBID TEXT REFERENCES md_DATABASES(DBID), 
	PRIMARY KEY (uUserID,uDBID)
); 

CREATE TABLE md_access (
	aUserID INTEGER REFERENCES md_USERS(UserID),
	aDBID TEXT REFERENCES md_DATABASES(DBID),
	attime TIMESTAMP, 
	download BOOLEAN, 
	PRIMARY KEY (aUserID, aDBID)
);

CREATE TABLE md_have_access (
	hUserID INTEGER REFERENCES md_USERS(UserID),
	hDBID TEXT REFERENCES md_DATABASES(DBID),
	hType accesstype,
	PRIMARY KEY (hUserID,hDBID)
);

CREATE TABLE md_owns (
	oUserID INTEGER REFERENCES md_USERS(UserID),
	oDBID TEXT REFERENCES md_DATABASES(DBID),
	PRIMARY KEY (oUserID,oDBID)
);""")

conn.commit()
conn.close()
