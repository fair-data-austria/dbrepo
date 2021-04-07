# Analyse Service

Service to analyse datatypes, create Metadatabase, extract metainfo from an SQL-script 

## API

### `POST /datatypesbypath`

A filepath to a CSV file is required. There are two optional parameters for determining ENUM types in a file: 

* enum, which is by default False 
* enum_tol, is used to distinguish between string and enum datatypes. 

Example http request: 
GET http://127.0.0.1:5000/datatypesbypath?filepath=testdt06.csv&enum=True&enum_tol=0.01

The reponse is a JSON object of the following form:

```JSON
{
    "columns": {
        "col1name": "Integer",
        "col2name": "Numeric", 
        "col3name": "Boolean", 
        "col4name": "Date", 
        "col5name": "String"
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
* flasgger
* messytables 
* pandas
* psycopg2-binary

They can also be installed using `pip install -r requirements.txt`

### Running (Development)
run `python3 app.py` add '/upload' to url 

### Running with Docker

Building the image: `sudo docker build -t analyse-service .`
