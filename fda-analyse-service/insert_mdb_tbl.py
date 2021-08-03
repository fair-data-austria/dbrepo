from psycopg2 import connect
import psycopg2.extras
import requests
import json

def insert_mdb_tbl(dbid): 
    # Get database info 
    try:
        s = requests.get(
        "http://fda-database-service:9092/api/database/",
        params = {"id":dbid}
        ).json()
    except Exception as e:
        print("Error while trying to get database info",e)

    # Conneting to database 
    try:
        conn=connect(
                dbname = s[0]['internalName'],
                user = "postgres", 
                host = 'fda-userdb-'+ s[0]['internalName'].replace('_', '-'),
                password = "postgres"
                )
        cursor = conn.cursor()

        # Extract table names, number of tables 
        cursor.execute("""SELECT columns.table_name, count(columns.column_name) as colcount, 
                       (select n_live_tup from pg_stat_user_tables where relname = columns.table_name) as rowcount
        FROM information_schema.columns 
        WHERE table_schema='public'
        GROUP BY columns.table_name;"""
        )
            
        res = cursor.fetchall()
        # Merge table names with dbid for inserting tuples 
        tblnames=[]
        for item in res:
            tblnames.append((item[1],item[2],dbid,item[0],))
        print(tblnames)

        conn.commit()
        conn.close()
    except Exception as e:
        print("Error while connecting to database.", e)
        
    try:
        # Connecting to metadatabase 
        conn=connect(
        dbname="fda", 
        user = "postgres",
        host = "fda-metadata-db",
        password = "postgres"
        )

        cursor = conn.cursor() 

        # Prepare and insert tblnames into table mdb_TABLES 
        cursor.execute("PREPARE stmt AS Update mdb_TABLES set (numcols,numrows,last_modified) = ($1,$2,current_timestamp) where tdbid = $3 and internal_name = $4")
        psycopg2.extras.execute_batch(cursor, 
                      """EXECUTE stmt (%s,%s,%s,%s)"""
                      , tblnames)
        cursor.execute("DEALLOCATE stmt")
        r = cursor.statusmessage
        conn.commit()
        conn.close()
    except Exception as e: 
        print("Error while connecting to metadatabase.",e)
    return json.dumps(r)
