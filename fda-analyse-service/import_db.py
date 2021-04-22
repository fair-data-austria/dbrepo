from psycopg2 import connect
import requests
import json

def import_db(cid, dbid, resourcetype, description, publisher,year,bool_open):  #todo contact - foreignkey
    
    # Get container info  
    r = requests.get(
	"http://fda-container-service:9091/api/container",
    params = {"id":cid}
    )
    
    #engine = r.json()['image']['repository']
    
    s = requests.get(
    "http://fda-database-managing-service:9092/api/database/",
    params = {"id":dbid}
    )

    # Connecting to database 
    conn=connect(
        dbname = s.json()['name'], 
        user = "postgres",
        host = r.json()['internalName'],
        #host = str('fda-userdb-'+r.json()['name']).replace(" ","-"),
        password = "postgres"
    )

    cursor = conn.cursor() 

    # Extract database title
    cursor.execute(f"""Select table_catalog 
    from information_schema.tables;"""
    )
    dbtitle = cursor.fetchone()

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
        tblnames.append((dbid, cid,item[0],item[1]))
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
        colnames.append((dbid, cid,item[0],item[1], item[2]))
    print(colnames)

    conn.close()

    # Connect to Meta database 
    conn=connect(
        dbname="fda", 
        user = "postgres",
        host = "fda-metadata-db", 
        password = "postgres"
    )

    cursor = conn.cursor() 

    # Insert into Table DATABASES 
    cursor.execute("""Update mdb_databases set (ResourceType,Description,Publisher,Year,is_public,last_modified) = 
    (%s,%s,%s,%s,%s,current_timestamp)
    where id=%s and container_id=%s;""", (resourcetype, description, publisher,year,bool_open,dbid,cid,))


    # Prepare and insert tblnames into table TABLES 
#    records_list_template = ','.join(['%s'] * len(tblnames))
#    insert_tblnames = 'Insert into mdb_TABLES (tDBID,tName,last_modified,NumCols) values {} ON Conflict (tbdid,tname) do update set
#    '.format(records_list_template)
#    cursor.execute(insert_tblnames,tblnames)
#
#    cursor.execute("Select * from Tables;")
#    for i, record in enumerate(cursor): 
#        print( record )
#    conn.commit()

    conn.close()
