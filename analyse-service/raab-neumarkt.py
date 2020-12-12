from psycopg2 import connect 
import requests
import json 

# Post request on FDA-Container-Managing Service
# CONTAINER_MANAGING_URL = "http://localhost:9091/api/createDatabaseContainer"
# r = requests.post(CONTAINER_MANAGING_URL, json = {  "ContainerName": "raabneumarkt", 
# 	"DatabaseName": "raabneumarkt",
# 	"MasterUser": "cm",
# 	"Password": "postgres"
# })

r = requests.get(
	"http://localhost:9091/api/getDatabaseContainerByContainerID", 
	params = {"containerID":"raabneumarkt"}
)

r.json()

conn=connect(
    dbname= r.json()['DbName'], 
    user = "postgres",
    host = r.json()['IpAddress'], 
    password = "postgres"
)

cursor = conn.cursor() 

with open('2020_11_19__15_45_00raab_neumarkt.sql', 'r', encoding="utf8", errors='ignore') as sqlfile:
    cursor.execute(sqlfile.read())

conn.commit()
conn.close()