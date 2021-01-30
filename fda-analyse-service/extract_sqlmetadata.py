from psycopg2 import connect
import requests
import json
#to-do flexible URL 

def extract_sqlmetadata(cname): 
    # Get container info  
    r = requests.get(
	"http://localhost:9091/api/getDatabaseContainerByContainerID", 
	params = {"containerID":cname}
    )
    
    # ContainerID 
    cid = r.json()['ContainerID']

    # Connecting to database 
    conn=connect(
        dbname = r.json()['DbName'], 
        user = "postgres",
        host = r.json()['IpAddress'], 
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
        dbname="metadatabase", 
        user = "postgres",
        host = r.json()['IpAddress'], 
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

# ==================================================================================================
# Ex. Container Info 
# ==================================================================================================
#   "ContainerID": "e1ee6ce7350f4a139d7c4617f7b0a210fb173edacead9a09e408c18c10f034fb",
#   "Created": "2020-12-05T16:02:11.3704761Z",
#   "ContainerName": "/Metadatabase",
#   "DbName": "metadatabase",
#   "Status": "running",
#   "IpAddress": "172.17.0.7"
