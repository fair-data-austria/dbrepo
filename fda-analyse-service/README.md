# Analyse Service

Service to analyse datatypes, update statistical properties of databases in the metadatabase, add metadata, e.g., data provenance, db description ... to the metadatabase

Remark: if you use swagger-ui, you can switch between /api-analyze.json and /api-mdb.json

## API-analyze

### `POST /determinedt`

A filepath to a CSV file is required. There are two optional parameters for determining ENUM types in a file:

* enum, which is by default False
* enum_tol, is used to distinguish between string and enum datatypes.

Example http request:
POST /determinedt HTTP/1.1
Content-Type: application/json
Host: 127.0.0.1:5000
Content-Length: 93

{
  "enum": true,
  "enum_tol": 0.1,
  "filepath": "/data/testdt08.csv",
  "seperator": ","
}

The response is a JSON object of the following form:

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

### `POST /determinepk`

A filepath to a CSV and the used seperator is required. 

Example http request:
POST /determinepk HTTP/1.1
Content-Type: application/json
Host: 127.0.0.1:5000
Content-Length: 58

{
  "filepath": "/data/testdt08.csv",
  "seperator": ","
}

The response is a JSON object of the following form:
```
{
"attributename_a": 1,
"attributename_b": 2,
"attributename_c": 3,
"attributename_d": 4
}
```

where the numbers represent a ranking of all the primary keys (consisting of one column). Columns with name 'id' are always ranked first as they are most likely chosen as primary key. 

## API-mdb

### `POST /update_mdb_db`

Updates attributes 'description', 'resourcetype' and 'publisher' of a certain database in the metadatabase.

Example http request:
POST /update_mdb_db HTTP/1.1
Content-Type: application/json
Host: 127.0.0.1:5000
Content-Length: 178

{
  "dbid": 1,
  "description": "Here goes a detailed description of the data set.",
  "publisher": "Geological Institute, University of Tokyo",
  "resourcetype": "Census Data"
}

The response is a status message, e.g. "\"UPDATE 1\"".

### `POST /update_mdb_db_ispublic`

Updates the attribute is_public in table mdb_databases in the metadatabase.

Example http request:
POST /update_mdb_db_ispublic HTTP/1.1
Content-Type: application/json
Host: 127.0.0.1:5000
Content-Length: 39

{
  "dbid": 1,
  "is_public": "false"
}

The respose is a status message.

### `POST /update_mdb_data_provenance`

Updates the data provenance of a provided dataset and stores information in the metadatabase.

### `POST /update_mdb_tbl`

Automatically updates the number of columns and rows of each table in a certain database in the repository and saves the information in the metadatabase (entity mdb_tables, attributes numcols and numrows).

Example http request:
POST /update_mdb_tbl HTTP/1.1
Content-Type: application/json
Host: 127.0.0.1:5000
Content-Length: 15

{
  "dbid": 1
}

### `POST /update_mdb_col`

Updates entity mdb_columns attributes (datatype, ordinal_position, is_nullable) and automatically updates mdb_columns_nom (attribute max_length), mdb_columns_num (min, max, mean, sd, histogram) and mdb_columns_cat (num_cat, cat_array). The attribute 'histogram' describes a equi-width histogram with a fix number of 10 buckets. The last value in this numeric array is the width of one bucket. The attribute cat_array contains an array with the names of the categories.

Example http request:
POST /update_mdb_col HTTP/1.1
Content-Type: application/json
Host: 127.0.0.1:5000
Content-Length: 39

{
  "cid": 1,
  "dbid": 1,
  "tid": 1
}

### `POST /update_mdb_col_num_siunit`

Updates attribute siunit (physical quatities for length, mass, ... ) in metadatabase.

Example http request:
POST /update_mdb_columns_num_siunit HTTP/1.1
Content-Type: application/json
Host: 127.0.0.1:5000
Content-Length: 56

{
  "cid": 1,
  "dbid": 1,
  "siunit": "m",
  "tid": 1
}

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