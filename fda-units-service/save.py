from psycopg2 import connect
import psycopg2.extras
import requests
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
        cursor.execute("PREPARE stmt AS INSERT INTO mdb_concepts (URI,name,created) VALUES ($1,$2,current_timestamp) ON CONFLICT (URI) DO NOTHING")
        psycopg2.extras.execute_batch(cursor, 
                      """EXECUTE stmt (%s,%s)"""
                      , (uri,c_name))
        cursor.execute("DEALLOCATE stmt")
        r = cursor.statusmessage
        conn.commit()
        conn.close()
    except Exception as e:
        print("Error while connecting to metadatabase.",e)
    return json.dumps(r)

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
        cursor.execute("PREPARE stmt AS INSERT INTO mdb_columns_concepts (cDBID,tID, cID,URI,created) VALUES ($1,$2,$3,$4,current_timestamp) ON CONFLICT (cDBID, tID, cID, URI) DO NOTHING")
        psycopg2.extras.execute_batch(cursor,
                      """EXECUTE stmt (%s,%s,%s,%s)"""
                      , (cdbid,tid,cid,uri))
        cursor.execute("DEALLOCATE stmt")
        r = cursor.statusmessage
        conn.commit()
        conn.close()
    except Exception as e: 
        print("Error while connecting to metadatabase.",e)
    return json.dumps(r)