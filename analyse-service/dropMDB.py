from psycopg2 import connect 
import requests
import json 


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

cursor.execute(f"""DROP SCHEMA public CASCADE;
CREATE SCHEMA public;""")

conn.commit()
conn.close()