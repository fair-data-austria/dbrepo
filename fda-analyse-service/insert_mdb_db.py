from psycopg2 import connect 
import requests
import json 

def insert_mdb_db(dbid, resourcetype, description, publisher):
    # Connect to Meta database 
    try: 
        conn=connect(
        dbname="fda", 
        user = "postgres",
        host = "fda-metadata-db", 
        password = "postgres"
        )
    
        cursor = conn.cursor() 
    
        # Insert into Table DATABASES 
        cursor.execute("""Update mdb_databases set (ResourceType,Description,Publisher,last_modified) = 
        (%s,%s,%s,current_timestamp)
        where id=%s;""", (resourcetype, description, publisher,dbid,))
        r = cursor.statusmessage
        conn.commit()
        conn.close()
    except Exception as e: 
        print("Error while connecting to mdb",e)
    return json.dumps(r)
        