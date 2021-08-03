from psycopg2 import connect 
import requests
import json 
import subprocess
import time
import docker 
import os
import tarfile

client = docker.DockerClient(base_url='unix://var/run/docker.sock')

def copy_to(src, dst):
    name, dst = dst.split(':')
    container = client.containers.get(name)

    os.chdir(os.path.dirname(src))
    srcname = os.path.basename(src)
    tar = tarfile.open(src + '.tar', mode='w')
    try:
        tar.add(srcname)
    finally:
        tar.close()

    data = open(src + '.tar', 'rb').read()
    container.put_archive(os.path.dirname(dst), data)
    
    container.exec_run('tar -xf ' + dst)


# Create postgres image
try: 
    r = requests.post(
    	"http://fda-container-service:9091/api/image/",
        json = {
          "defaultPort": 5432,
          "environment": [
            {
              "key": "POSTGRES_USER",
              "value": "postgres"
            },
            {
              "key": "POSTGRES_PASSWORD",
              "value": "postgres"
            }
          ],
          "repository": "postgres",
          "tag": "latest"}
    )
except Exception as e: 
    print("Something went wrong on creating an image.",e)

time.sleep(3)
    
r = None
# Create docker container 
try: 
    r = requests.post(
    	"http://fda-container-service:9091/api/container/",
        json = {
          "name": "Example Database",
          "repository": "postgres",
          "tag": "latest"}
        )
    u = requests.put(
        "http://fda-container-service:9091/api/container/"+str(r.json()['id']),
        json = {
                "action": "START"
                }
        )
    
except Exception as e: 
    print("Something went wrong on creating a container.",e)
    
time.sleep(5)
    
s = None
# Create a database    
try: 
    s = requests.post(
        "http://fda-database-service:9092/api/database/", 
        json = { 
            "containerId": r.json()['id'],
            "name": "example database"}
            )
except Exception as e: 
    print("Something went wrong on creating a database. ", e)
    
time.sleep(5)

# Connect to database and create schema
try: 
    conn=connect( 
            dbname = s.json()['internalName'], 
            user = "postgres", 
            host = 'fda-userdb-'+ s.json()['internalName'].replace('_', '-'), 
            password = "postgres"
            )
    cursor = conn.cursor() 
    sql_file = open('./ex-dbs/create_ex_db.sql','r')
    cursor.execute(sql_file.read())
    conn.commit()
    
    copy_to('/ex-dbs', str(r.json()['internalName']).replace('_','-') + ':/ex-dbs')
    #copy_to('/ex-dbs/insert_ex_db.sql', str(r.json()['internalName']).replace('_','-')+':/insert_ex_db.sql')
    
    insert_file = open('/ex-dbs/insert_ex_db.sql', 'r')
    cursor.execute(insert_file.read())
    conn.commit()
    conn.close()
except Exception as e: 
    print("Error while connecting to database.", e) 

