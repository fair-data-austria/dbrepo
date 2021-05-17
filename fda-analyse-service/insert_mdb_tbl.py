from psycopg2 import connect 
import requests
import json 

############# Baustelle #######################################################

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
                dbname = s.json()[0]['internalName'], 
                user = "postgres", 
                host = 'fda-userdb-'+ s.json()[0]['internalName'].replace('_', '-'), 
                password = "postgres"
                )
        cursor = conn.cursor() 
        
        # Extract table names, number of tables 
        cursor.execute(f"""SELECT columns.table_name, count(columns.column_name)
        FROM information_schema.columns 
        WHERE table_schema='public'
        GROUP BY columns.table_name;"""
        )
            
        res = cursor.fetchall()
        # Merge table names with containerid for inserting tuples 
        tblnames=[]
        for item in res:
            tblnames.append((cid,item[0],item[1]))
        print(tblnames)
    
        # Extract column information 
        cursor.execute(f"""SELECT columns.table_name, columns.column_name, columns.data_type 
        FROM information_schema.columns 
        WHERE table_schema='public';"""
        )
        res2 = cursor.fetchall() 
        # Merge column information with containerid for inserting tuples 
        colnames=[]
        for item in res2:
            colnames.append((cid,item[0],item[1], item[2]))
        print(colnames)    
    
        conn.commit()
        conn.close()
    except Exception as e: 
        print("Error while connecting to database.", e) 