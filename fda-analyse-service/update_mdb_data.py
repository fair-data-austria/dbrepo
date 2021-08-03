from psycopg2 import connect 
import requests
import json 

def update_mdb_data(dataid, provenance):
    # Connect to Meta database 
    try: 
        conn=connect(
        dbname="fda", 
        user = "postgres",
        host = "fda-metadata-db", 
        password = "postgres"
        )
    
        cursor = conn.cursor() 
    
        # Update Table DATA in MDB  
        cursor.execute("""Update mdb_data set (PROVENANCE) = 
        (%s)
        where id=%s;""", (provenance,dataid,))
        r = cursor.statusmessage
        conn.commit()
        conn.close()
    except Exception as e: 
        print("Error while connecting to mdb",e)
    return json.dumps(r)
        