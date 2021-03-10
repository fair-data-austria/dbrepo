# Analyse Service

Service to analyse datatypes, create Metadatabase, extract metainfo from an SQL-script 

## API

### `POST /upload`

A CSV file (multipart/formdata) is uploaded.

The reponse is a JSON object of the following form:

```JSON
{
    "columns": {
        "int": "Integer",
        "float": "Numeric", 
        "boolean": "Boolean", 
        "date": "Date", 
        "text": "String"
    }
}
```

### `POST /extract-metadata`

Response is JSON object of the following form 

```JSON
{
	"cname": "testdb"
}
```

where cname is the container name of a Docker container. 

## How to use 
### Dependencies 
* python3 
* flask
* messytables 
* sql-metadata
* psycopg2-binary

They can also be installed using `pip install -r requirements.txt`

### Running (Development)
run `python3 app.py` add '/upload' to url 

### Running with Docker

Building the image: `sudo docker build -t analyse-at.tuwien.service .`
