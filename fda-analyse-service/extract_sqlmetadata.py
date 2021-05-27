from psycopg2 import connect
import requests
import json

def extract_sqlmetadata(cid): 
    # Get container info  
    r = requests.get(
	"http://fda-container-managing-service:9091/api/container",
    params = {"id":cid}
    )
    
    # ContainerID 
    #cid = r.json()['id']

    # Connecting to database 
    conn=connect(
        dbname = r.json()['internalName'], 
        user = "postgres",
        host = r.json()['adresses'][0]['ipv4'],
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

    conn.close()

    # Connect to Meta database 
    r = requests.get(
        "http://localhost:9091/api/getDatabaseContainerByContainerID", 
        params = {"containerID":"Metadatabase"}
    )

    r.json()

    conn=connect(
        dbname="fda", 
        user = "postgres",
        host = "fda-metadata-db", 
        password = "postgres"
    )

    cursor = conn.cursor() 

    # Insert into Table DATABASES 
    cursor.execute("Insert into DATABASES (DBID,Title) values (%s,%s) ON CONFLICT (DBID) DO UPDATE SET Title=EXCLUDED.Title;", (cid, dbtitle))
    cursor.execute("Select * from Databases;")
    for i, record in enumerate(cursor): 
        print( record )
    conn.commit()

    # Prepare and insert tblnames into table TABLES 
    records_list_template = ','.join(['%s'] * len(tblnames))
    insert_tblnames = 'Insert into TABLES (tDBID,tName,NumCols) values {} ON Conflict do nothing'.format(records_list_template)
    cursor.execute(insert_tblnames,tblnames)

    cursor.execute("Select * from Tables;")
    for i, record in enumerate(cursor): 
        print( record )
    conn.commit()

    # Prepare and insert into table COLUMNS 
    records_list_template = ','.join(['%s'] * len(colnames))
    insert_colnames = 'Insert into COLUMNS (cDBID,tName,cName,Datatype) values {} ON Conflict do nothing'.format(records_list_template)
    cursor.execute(insert_colnames,colnames)

    cursor.execute("Select * from Columns;")
    for i, record in enumerate(cursor): 
        print( record )
    conn.commit()

    conn.close()

