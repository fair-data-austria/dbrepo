import json  
import numpy as np
import messytables, pandas as pd
from messytables import CSVTableSet, headers_guess
from determine_dt import determine_datatypes
from psycopg2 import connect
import requests
   
def analysecsv(path,seper,internaldbname, dbhost, dbid, tname, header = True): 
    # Connect to Meta database 
    try: 
        conn=connect(
            dbname="fda", 
            user = "postgres",
            host = "fda-metadata-db", 
            password = "postgres"
        )
        
        cursor = conn.cursor() 
    except (Exception, psycopg2.DatabaseError) as error:
        print(error)
  
    r={}
        
    # Check if csv format is suitable 
    if header == True: 
        df = pd.read_csv(path,sep=seper)
    else: 
        df = pd.read_csv(path,sep=seper,header=None)
        
    csvcol = df.shape[1]
    cursor.execute(f"""select numcols from mdb_tables;"""
    )
    mdcol = cursor.fetchone()
    
    if csvcol != mdcol: 
        r["dim"] = "Dimension mismatch. Specify which colums should be filled."

    # Check if determined datatypes matches 
    dt = json.loads(determine_datatypes(path,seperator=seper)) 
        
    if header == True: 
        fh = open(path, 'rb')
    
        # Load a file object:
        table_set = CSVTableSet(fh)
        
        # A table set is a collection of tables:
        row_set = table_set.tables[0]
           
        # guess header names and the offset of the header:
        offset, headers = headers_guess(row_set.sample)
        
        for i in dt["columns"].keys(): 
            cursor.execute("select datatype from mdb_columns where cdbid = %s and tname = %s and cname = %s;""", (dbid, tname, i, ))
            res = cursor.fetchone()
            if res != dt["columns"][i]: 
                r["dt"] = "Datatype mismatch in {}. {} vs {}".format(i,res,dt["columns"][i])
        conn.close()
    else: 
        conn=connect(
            dbname=internaldbname, 
            user = "postgres",
            host = dbhost,
            password = "postgres"
        )
        
        cursor = conn.cursor() 
        cursor.execute("select ordinal_position, data_type from information_schema.columns where table_name = %s;",(tname,))
        pos_dt = cursor.fetchall()
        ldt = list(dt["columns"].values())
        
        for i in range(0,len(ldt)): 
            if pos_dt[i][1].lower() != ldt[i].lower(): 
                r["dt"] = "Datatype mismatch at ordinal position {}".format(i+1)
        conn.close()
          

    # Check constraints (Primary key, Foreignkey, nullable, other constraints?)
    conn=connect(
        dbname=internaldbname, 
        user = "postgres",
        host = dbhost,
        password = "postgres"
    )
    
    cursor = conn.cursor() 
    # Get ordinal position of primary key attributes
    cursor.execute("""SELECT c.ordinal_position
                   FROM information_schema.table_constraints tc 
                   JOIN information_schema.constraint_column_usage AS ccu USING (constraint_schema, constraint_name) 
                   JOIN information_schema.columns AS c ON c.table_schema = tc.constraint_schema
                   AND tc.table_name = c.table_name AND ccu.column_name = c.column_name
                   WHERE constraint_type = 'PRIMARY KEY' and tc.table_name = %s;""",(tname,))
    pk = cursor.fetchall()
    pk_flattend = [item for items in pk for item in items]
    pk_aditer = list(map(lambda x: x -1, pk_flattend))
    
    tmp = df[df.iloc[:,np.r_[pk_aditer]].duplicated()]
    if not tmp.empty: 
        r["pk"] = "Rows {} violate primary key".format(tmp)
    
    # detect enum values
    
    return json.dumps(r)
       