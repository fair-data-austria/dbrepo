from psycopg2 import connect 
import requests
import json 

Post request on FDA-Container-Managing Service
CONTAINER_MANAGING_URL = "http://localhost:9091/api/createDatabaseContainer"
r = requests.post(CONTAINER_MANAGING_URL, json = {  "ContainerName": "testdb", 
	"DatabaseName": "testdb",
	"MasterUser": "cm",
	"Password": "postgres"
})

r = requests.get(
	"http://localhost:9091/api/getDatabaseContainerByContainerID", 
	params = {"containerID":"testdb"}
)

r.json()

conn=connect(
    dbname=r.json()['DbName'], 
    user = "postgres",
    host = r.json()['IpAddress'], 
    password = "postgres"
)

cursor = conn.cursor() 

cursor.execute(f"""Select * from test2;"""
)

#cursor.execute("Delete from test;")

for i, record in enumerate(cursor): 
    #s.append(record)
    print( record )
conn.commit()
conn.close()