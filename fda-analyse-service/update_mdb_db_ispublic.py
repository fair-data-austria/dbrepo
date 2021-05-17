from psycopg2 import connect 
import requests
import json 

def insert_mdb_db_pub(dbid, ispublic):
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
        cursor.execute("""Update mdb_databases set (is_public,last_modified) = 
        (%s,current_timestamp)
        where id=%s;""", (ispublic,dbid,))
        conn.commit()
        conn.close()
    except Exception as e: 
        print("Error while connecting to mdb",e)
        