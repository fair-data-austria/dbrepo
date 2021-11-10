# Search Service

Supports ElasticSearch for searching databases, tables, colums saved in the metadatabase. Two indices were created, namely databaseindex and tblindex. For a detailed documentation also cf. https://www.elastic.co/guide/index.html

### `POST /9200/databaseindex/_search`
with JSON body
```JSON
{
  "query": {
    "match": {
      "name": {
        "query": "Whether whorld",
	"fuzziness": 6
      }
    }
  }
}
```

Finds databases by their names. 

Example output: 
```JSON
{
  "took": 76,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 1,
      "relation": "eq"
    },
    "max_score": 0.2054872,
    "hits": [
      {
        "_index": "databaseindex",
        "_type": "_doc",
        "_id": "1",
        "_score": 0.2054872,
        "_source": {
          "_class": "at.tuwien.entities.database.Database",
          "id": 1,
          "container": {
            "id": 1,
            "containerCreated": 1633772448088,
            "name": "Weather World",
            "internalName": "fda-userdb-weather-world",
            "hash": "789905c2c184ffdadb80d1a0158b3e282404c25bba136731afc3c9ade7126ba1",
            "port": 37665,
            "image": {
              "_class": "at.tuwien.entities.container.image.ContainerImage$HibernateProxy$dVABPDQa"
            },
            "created": 1633772448249,
            "lastModified": 1633772448249
          },
          "name": "Weather Australia",
          "internalName": "weather_australia",
          "description": "string",
          "isPublic": true,
          "created": 1633772487671,
          "lastModified": 1633772487671
        }
      }
    ]
  }
}
```

### `POST /9200/tblindex/_search`
```JSON
{
  "query": {
    "match": {
      "columns.name": {
        "query": "date",
	"fuzziness": 6
      }
    }
  }
}
```

Finds databases with columnnames 'date'. 
