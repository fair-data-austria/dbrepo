from psycopg2 import connect
import psycopg2.extras
import json

def insert_mdb_concepts(uri, c_name): 
    try:
        # Connecting to metadatabase 
        conn=connect(
        dbname="fda",
        user = "postgres",
        host = "fda-metadata-db",
        password = "postgres"
        )

        cursor = conn.cursor()

        # Insert tblnames into table mdb_TABLES 
        cursor.execute("INSERT INTO mdb_concepts (URI,name,created) VALUES (%s,%s,current_timestamp) ON CONFLICT (URI) DO NOTHING", (uri,c_name))
        r = cursor.rowcount
        conn.commit()
        conn.close()
    except Exception as e:
        print("Error while connecting to metadatabase.",e)
    return r

def insert_mdb_columns_concepts(cdbid,tid, cid, uri):
    try:
        # Connecting to metadatabase
        conn=connect(
        dbname="fda",
        user = "postgres",
        host = "fda-metadata-db",
        password = "postgres"
        )

        cursor = conn.cursor()

        # Insert tblnames into table mdb_TABLES
        cursor.execute("INSERT INTO mdb_columns_concepts (cDBID,tID, cID,URI,created) VALUES (%s,%s,%s,%s,current_timestamp) ON CONFLICT (cDBID, tID, cID) DO UPDATE SET uri = EXCLUDED.uri", (cdbid,tid,cid,uri))
        r = cursor.rowcount
        conn.commit()
        conn.close()
    except Exception as e: 
        print("Error while connecting to metadatabase.",e)
    return r
