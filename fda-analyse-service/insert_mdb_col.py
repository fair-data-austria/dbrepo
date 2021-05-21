from psycopg2 import connect 
import psycopg2.extras
from sqlalchemy import create_engine, text
import requests
import json 

def insert_mdb_col(dbid, tid): 
    # Get database info 
    try: 
        s = requests.get(
        "http://fda-database-service:9092/api/database/",
        params = {"id":dbid}
        ).json()
    except Exception as e: 
        print("Error while trying to get database info",e)
        
    # Get tablename by dbid and tid 
    try: 
        tbl_info = requests.get(
        "http://fda-table-service:9094/api/database/{0}/table/{1}/".format(dbid,tid)).json()
        tbl_name = tbl_info['internalName']
    except Exception as e: 
        print("Error:", e)
    
    # to-do case distiction between type of engine 
      
    # Conneting to database 
    try: 
        engine = create_engine('postgresql+psycopg2://postgres:postgres@fda-userdb-'+s[0]['internalName'].replace('_', '-')+'/'+s[0]['internalName'])
        
        sql = text("SELECT column_name, columns.data_type, columns.ordinal_position, is_nullable from information_schema.columns where columns.table_name= :tblname")
        
        with engine.begin() as conn: 
            res = conn.execute(sql, {"tblname": tbl_name}).fetchall()
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
        
        # Prepare and insert into table mdb_COLUMNS 
        tuplelist = [tuple([dbid,tid]+list(tup)) for tup in res]
        cursor.execute("""PREPARE stmt INSERT INTO mdb_COLUMNS (cDBID,tID,cName,datatype,ordinal_position,null_constraint) values ($1,$2,$3,$4,$5,$6)
                       ON CONFLICT (cDBID,tID) DO UPDATE set (cName, datatype,ordinal_position,null_constraint) = ($3,$4,$5,$6)""")
        psycopg2.extras.execute_batch(cursor, 
                        """EXECUTE stmt (%s,%s,%s,%s,%s,%s)""", 
                        tuplelist)
        cursor.execute("DEALLOCATE stmt")
        
#        psycopg2.extras.execute_values(cursor, 
#                        """INSERT INTO mdb_COLUMNS (cDBID,tID,cName,datatype,ordinal_position,null_constraint) values %s""", 
#                        tuplelist)
        conn.commit()
        conn.close()
    except Exception as e: 
        print("Error while connecting to metadatabase.",e)
