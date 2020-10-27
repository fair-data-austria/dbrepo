# Analyse Service

Service to analyse datatypes ...

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

## How to use 
### Dependencies 
* python3 
* flask
* messytables 

They can also be installed using `pip install -r requirements.txt`

### Running (Development)
run `python3 app.py`

### Running with Docker

Building the image: `sudo docker build -t analyse-service .`