from psycopg2 import connect 
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
        
        i = 0
        for item in tuplelist: 
            cursor.execute("insert into mdb_columns (cDBID,tID,cName,datatype,ordinal_position,check_expression) values (%s,%s,%s,%s,%s,%s) returning id",item)
            cid = cursor.fetchone()[0]
            tuplelist[i] = tuple([cid]+list(tuplelist[i]))
            i = i+1
        
        ret = cursor.statusmessage

        conn.commit()
        
        nomdtlist = ['text','character varying','varchar','char']
        numdtlist = ['bigint','integer','smallint','decimal','numeric','real','double precision']
#       catdtlist = ['boolean','enum','USER-DEFINED']
        
        if 'INSERT' in ret: 
            for item in tuplelist: 
                if item[4] in nomdtlist: 
                    insert_mdb_nomcol(tuplelist[0][1],tuplelist[0][2],tuplelist[0][0])
                if item[4] in numdtlist: 
                    insert_mdb_numcol(tuplelist[0][1],tuplelist[0][2],tuplelist[0][0])
#                if item[4] in catdtlist: 
#                    insert_mdb_catcol(tuplelist[0][1],tuplelist[0][2],tuplelist[0][0])
                    
        conn.close()
    except Exception as e: 
        print("Error while connecting to metadatabase.",e)
    return json.dumps(ret)
        
def insert_mdb_nomcol(dbid,tid,cid): 
    # Connecting to metadatabase - to obtain column name
    try: 
        conn=connect(
        dbname="fda", 
        user = "postgres",
        host = "fda-metadata-db", 
        password = "postgres"
        )
        
        cursor = conn.cursor() 
        cursor.execute("select cDBID,tID,ID,cName from mdb_columns where cdbid = %s and tid = %s and cid =%s",(dbid,tid,cid))
        res = cursor.fetchall()
        cname = res[0][3]
    except Exception as e: 
        print("Error while connecting to metadatabase.",e)
    
    # Connect to database - to obtain max_length 
    try: 
        s = requests.get(
        "http://fda-database-service:9092/api/database/",
        params = {"id":dbid}
        ).json()
    except Exception as e: 
        print("Error while trying to get database info",e)
    try: 
        tbl_info = requests.get(
        "http://fda-table-service:9094/api/database/{0}/table/{1}/".format(dbid,tid)).json()
        tbl_name = tbl_info['internalName']
    except Exception as e: 
        print("Error:", e)
    try: 
        engine = create_engine('postgresql+psycopg2://postgres:postgres@fda-userdb-'+s[0]['internalName'].replace('_', '-')+'/'+s[0]['internalName'])
        
        sql = text("select min(char_length(" + cname + ")) from " + tbl_name )
        
        with engine.begin() as conn: 
            res = conn.execute(sql).fetchone()
        maxlen = res[0]
    except Exception as e: 
        print("Error while connecting to database",e)
    try: 
        conn=connect(
        dbname="fda", 
        user = "postgres",
        host = "fda-metadata-db", 
        password = "postgres"
        )
        
        cursor = conn.cursor() 
        cursor.execute("""Insert into mdb_columns_nom (cdbid,tid,cid,maxlength,last_modified) 
            values (%s,%s,%s,%s,current_timestamp)
            ON CONFLICT (cdbid,tid,cid) do update set (maxlength,last_modified) = (%s,current_timestamp)""",(dbid,tid,cid,maxlen,maxlen,))

        ret = cursor.statusmessage 
        cursor.commit()  
    except Exception as e: 
        print("Error while inserting into metadatabase",e)
    return json.dumps(ret)

def insert_mdb_numcol(dbid,tid,cid): 
    # Connecting to metadatabase to obtain columnname
    try: 
        conn=connect(
        dbname="fda", 
        user = "postgres",
        host = "fda-metadata-db", 
        password = "postgres"
        )
        
        cursor = conn.cursor() 
        cursor.execute("select cDBID,tID,ID,cName from mdb_columns where cdbid = %s and tid = %s and cid =%s",(dbid,tid,cid))
        res = cursor.fetchall()
        cname = res[0][3]
    except Exception as e: 
        print("Error while connecting to metadatabase.",e)
    try: 
        s = requests.get(
        "http://fda-database-service:9092/api/database/",
        params = {"id":dbid}
        ).json()
    except Exception as e: 
        print("Error while trying to get database info",e)
    try: 
        tbl_info = requests.get(
        "http://fda-table-service:9094/api/database/{0}/table/{1}/".format(dbid,tid)).json()
        tbl_name = tbl_info['internalName']
    except Exception as e: 
        print("Error:", e)    
    # Determine min, max, ... 
    try: 
        engine = create_engine('postgresql+psycopg2://postgres:postgres@fda-userdb-'+s[0]['internalName'].replace('_', '-')+'/'+s[0]['internalName'])
        
        # min
        sql = text("select min(" + cname + ") from " + tbl_name )
        with engine.begin() as conn: 
            res = conn.execute(sql).fetchone()
        minval = res[0]
        
        # max
        sql = text("select max(" + cname + ") from " + tbl_name )
        with engine.begin() as conn: 
            res = conn.execute(sql).fetchone()
        maxval = res[0]
        
        # mean 
        sql = text("select avg(" + cname + ") from " + tbl_name )
        with engine.begin() as conn: 
            res = conn.execute(sql).fetchone()
        meanval = res[0]
        
        # sd 
        sql = text("select stddev(" + cname + ") from " + tbl_name )
        with engine.begin() as conn: 
            res = conn.execute(sql).fetchone()
        sdval = float(res[0])
        
        # histogram 
        sql = text("select " + cname + "from " + tbl_name + "where rand() <= 0.3")
        #to-do 
        

    except Exception as e: 
        print("Error while connecting to database",e)
    # Insert / update values in metadata-db 
    try: 
        conn=connect(
        dbname="fda", 
        user = "postgres",
        host = "fda-metadata-db", 
        password = "postgres"
        )
        
        cursor = conn.cursor() 
        cursor.execute("""Insert into mdb_columns_num (cdbid,tid,cid,minval,maxval,mean,sd,last_modified) 
            values (%s,%s,%s,%s,%s,%s,%s,%s,current_timestamp)
            ON CONFLICT (cdbid,tid,cid) do update set 
            (minval,maxval,mean,sd,last_modified) = (%s,%s,%s,%s,%s,current_timestamp)""",
            (dbid,tid,cid,minval,maxval,meanval,sdval,minval,maxval,meanval,sdval,))

        ret = cursor.statusmessage 
        cursor.commit()  
    except Exception as e: 
        print("Error while inserting into metadatabase",e)
    return json.dumps(ret)

#def insert_mdb_catcol(dbid,tid,cid): 
#    # Connecting to metadatabase to obtain columnname
#    try: 
#        conn=connect(
#        dbname="fda", 
#        user = "postgres",
#        host = "fda-metadata-db", 
#        password = "postgres"
#        )
#        
#        cursor = conn.cursor() 
#        cursor.execute("select cDBID,tID,ID,cName from mdb_columns where cdbid = %s and tid = %s and cid =%s",(dbid,tid,cid))
#        res = cursor.fetchall()
#        cname = res[0][3]
#    except Exception as e: 
#        print("Error while connecting to metadatabase.",e)
#    try: 
#        s = requests.get(
#        "http://fda-database-service:9092/api/database/",
#        params = {"id":dbid}
#        ).json()
#    except Exception as e: 
#        print("Error while trying to get database info",e)
#    try: 
#        tbl_info = requests.get(
#        "http://fda-table-service:9094/api/database/{0}/table/{1}/".format(dbid,tid)).json()
#        tbl_name = tbl_info['internalName']
#    except Exception as e: 
#        print("Error:", e)    
#    # Determine number of categories, categories array 
#    try: 
#        engine = create_engine('postgresql+psycopg2://postgres:postgres@fda-userdb-'+s[0]['internalName'].replace('_', '-')+'/'+s[0]['internalName'])
#        
#        # num_categories
#        # TO-DO 
#    except Exception as e: 
#        print("Error:",e)
        
def update_mdb_siunit(dbid,tid,cid,siunit): 
    # Insert / update values in metadata-db 
    try: 
        conn=connect(
        dbname="fda", 
        user = "postgres",
        host = "fda-metadata-db", 
        password = "postgres"
        )
        
        cursor = conn.cursor() 
        cursor.execute("""Update mdb_columns_num set (cdbid,tid,cid,siunit,last_modified) 
            = (%s,%s,%s,%s,current_timestamp)""",
            (dbid,tid,cid,siunit,))

        ret = cursor.statusmessage 
        cursor.commit()  
    except Exception as e: 
        print("Error while inserting into metadatabase",e)
    return json.dumps(ret)