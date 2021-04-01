from psycopg2 import connect 
import requests
import json 

# Post request on FDA-Container-Managing Service
CONTAINER_MANAGING_URL = "http://localhost:9091/api/createDatabaseContainer"
r = requests.post(CONTAINER_MANAGING_URL, json = {  "ContainerName": "Metadatabase", 
	"DatabaseName": "metadatabase",
	"MasterUser": "cm",
	"Password": "postgres"
})

r = requests.get(
	"http://localhost:9091/api/getDatabaseContainerByContainerID", 
	params = {"containerID":"Metadatabase"}
)

r.json()

conn=connect(
    dbname="metadatabase", 
    user = "postgres",
    host = r.json()['IpAddress'], 
    password = "postgres"
)

cursor = conn.cursor() 

cursor.execute(f"""CREATE SEQUENCE seq_data;
CREATE SEQUENCE seq_user; 

CREATE TABLE DATA ( 
	ID INTEGER PRIMARY KEY DEFAULT nextval('seq_data'), 
	PROVENANCE TEXT, 
	FileEncoding TEXT, 
	FileType VARCHAR(100),
	Version TEXT,
	Seperator TEXT
);  

CREATE TABLE USERS ( 
	UserID INTEGER PRIMARY KEY DEFAULT nextval('seq_user'),
	Name VARCHAR(50)
); 

CREATE TABLE CONTACTPERSON (
	cUserID INTEGER PRIMARY KEY REFERENCES USERS(UserID),
	Email TEXT
);

CREATE TABLE DATABASES ( 
	DBID TEXT PRIMARY KEY, -- (= DockerContainer ID)
	Title VARCHAR(50), 
	ResourceType TEXT, 
	Description TEXT, 
	Engine VARCHAR(20) DEFAULT 'Postgres', 
	Publisher VARCHAR(50), 
	Year DATE DEFAULT CURRENT_DATE, 
	Open BOOLEAN DEFAULT TRUE, 
	Contact INTEGER REFERENCES CONTACTPERSON(cUserID)
); 

CREATE TABLE TABLES ( 
	tDBID TEXT REFERENCES DATABASES(DBID), 
	tName VARCHAR(50), 
	NumCols INTEGER, 
	NumRows INTEGER, 
	Version TEXT,
	PRIMARY KEY(tDBID,tName)
);

CREATE TABLE COLUMNS ( 
	cDBID TEXT NOT NULL, 
	tName VARCHAR(50) NOT NULL, 
	cName VARCHAR(50), 
	Datatype VARCHAR(50), 
	SIunit TEXT, 
	MaxVal NUMERIC, 
	MinVal NUMERIC , 
	FOREIGN KEY (cDBID,tName) REFERENCES TABLES(tDBID,tName),
	PRIMARY KEY(cDBID, tName, cName)
);

CREATE TABLE VIEW ( 
	vDBID TEXT REFERENCES Databases(DBID), 
	vName VARCHAR(50), 
	Query TEXT, 
	Public BOOLEAN , 
	NumCols INTEGER, 
	NumRows INTEGER, 
	PRIMARY KEY (vDBID,vName)
);

CREATE TABLE feed ( 
	fDBID TEXT, 
	fName VARCHAR(50),
	fUserId INTEGER REFERENCES USERS(UserID), 
	fDataID INTEGER REFERENCES DATA(ID), 
	FOREIGN KEY (fDBID,fName) REFERENCES TABLES(tDBID,tNAME), 
	PRIMARY KEY (fDBID,fName,fUserId, fDataID)
);

CREATE TABLE update ( 
	uUserID INTEGER REFERENCES USERS(UserID),
	uDBID TEXT REFERENCES Databases(DBID), 
	PRIMARY KEY (uUserID,uDBID)
); 

CREATE TABLE access (
	aUserID INTEGER REFERENCES USERS(UserID),
	aDBID TEXT REFERENCES Databases(DBID),
	attime TIMESTAMP, 
	download BOOLEAN, 
	PRIMARY KEY (aUserID, aDBID, attime, download)
);""")

conn.commit()
conn.close()